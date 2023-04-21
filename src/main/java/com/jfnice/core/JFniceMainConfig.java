package com.jfnice.core;

import com.Start;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.alibaba.fastjson.parser.ParserConfig;
import com.jfinal.config.*;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.template.Engine;
import com.jfinal.template.ext.directive.NowDirective;
import com.jfnice.admin.AdminRoutes;
import com.jfnice.admin.asset.AssetService;
import com.jfnice.admin.asset.AssetTask;
import com.jfnice.admin.dict.DictKit;
import com.jfnice.cache.JsyCache;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.JsyJsonFactory;
import com.jfnice.ext.SqlTplAuto;
import com.jfnice.ext.StaticRoute;
import com.jfnice.handler.DruidStatViewExtendHandler;
import com.jfnice.handler.ShiroHandler;
import com.jfnice.handler.XssRequestExtendHandler;
import com.jfnice.kit.UrlKit;
import com.jfnice.model._MappingKit;
import com.school.demo.DemoRoutes;
import com.school.library.route.LibraryRoutes;

import java.sql.Connection;
import java.util.List;

public class JFniceMainConfig extends JFinalConfig {

    static Prop p;

    private List<Routes> routesList = null;

    /**
     * PropKit.useFirstFound(...) 使用参数中从左到右最先被找到的配置文件 从左到右依次去找配置，找到则立即加载并立即返回，后续配置将被忽略
     */
    static void loadConfig() {
        if (p == null) {
            p = PropKit.useFirstFound("config.properties");
        }
    }

    /**
     * 抽取成独立的方法，例于 _Generator 中重用该方法，减少代码冗余
     */
    public static DruidPlugin createDruidPlugin() {
        loadConfig();
        return new DruidPlugin(p.get("jdbcUrl").trim(), p.get("user").trim(), p.get("password").trim());
    }

    /**
     * 配置常量
     */
    public void configConstant(Constants me) {
        loadConfig();

        // 支持 Controller、Interceptor、Validator 之中使用 @Inject 注入业务层，并且自动实现 AOP
        me.setInjectDependency(true);
        // 配置对超类中的属性进行注入
        me.setInjectSuperClass(true);
        // 配置上传属性
        me.setMaxPostSize(AssetService.UPLOAD_MAX_SIZE);
        me.setBaseUploadPath(PathKit.getWebRootPath() + "\\upload");
        me.setBaseDownloadPath(PathKit.getWebRootPath() + "\\upload");

//        me.setJsonFactory(MixedJsonFactory.me());
        me.setJsonFactory(new JsyJsonFactory()); // 转json字符时将驼峰转换成下划线

        //fastjson增加safeMode
        ParserConfig.getGlobalInstance().setSafeMode(true);

        me.setDevMode(p.getBoolean("devMode", false));
    }

    /**
     * 配置路由
     */
    public void configRoute(Routes me) {
        routesList = Routes.getRoutesList();
        me.add(new AdminRoutes());
        me.add(new DemoRoutes());
        me.add(new LibraryRoutes());
    }

    /**
     * 配置Template Engine
     */
    public void configEngine(Engine me) {
        me.setDevMode(false);
        me.addSharedObject("DictKit", new DictKit());
        me.addSharedObject("JsonKit", new JsonKit());
        me.addDirective("Now", NowDirective.class);
        me.addSharedMethod(new UrlKit());
    }

    /**
     * 配置插件
     */
    public void configPlugin(Plugins me) {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setDbType("sqlserver");
        WallConfig wallConfig = new WallConfig("META-INF/druid/wall/sqlserver");
        wallConfig.setSelectUnionCheck(false);//让 druid 允许在 sql 中使用 union
        wallFilter.setConfig(wallConfig);

        // 配置 druid 数据库连接池插件
        DruidPlugin druidPlugin = createDruidPlugin();
        druidPlugin.addFilter(wallFilter);
        druidPlugin.addFilter(new StatFilter());
        druidPlugin.setMaxPoolPreparedStatementPerConnectionSize(20);
        me.add(druidPlugin);


        // 配置ActiveRecord插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        arp.setTransactionLevel(Connection.TRANSACTION_READ_COMMITTED);
//        arp.setShowSql(Start.showSql);
        arp.setShowSql(false);
        arp.setDialect(new SqlServerDialect());
        // 所有映射在 MappingKit 中自动化搞定
        _MappingKit.mapping(arp);
        arp.setPrimaryKey("setting", "access, user_id");
        arp.getEngine().addSharedObject("DictKit", new DictKit());
        // 扫描添加sql文件
        SqlTplAuto.addAllSqlTemplate(arp, PathKit.getRootClassPath());
        me.add(arp);

        Cron4jPlugin cp = new Cron4jPlugin();
        cp.addTask("30 2 * * *", new AssetTask()); // 定期清理资源任务
        me.add(cp);

        if (JsyCache.isRedis()) {
            RedisPlugin newsRedis = new RedisPlugin("redis",
                    p.get("redis.hosts"),
                    p.getInt("redis.port", 6379),
                    p.getInt("redis.timeout", 2000),
                    p.get("redis.password"),
                    p.getInt("redis.database", 0));
            me.add(newsRedis);
        } else if (JsyCache.isEhCache()) {
            me.add(new EhCachePlugin());
        }
    }

    /**
     * 配置全局拦截器
     */
    public void configInterceptor(Interceptors me) {

    }

    /**
     * 配置处理器
     */
    public void configHandler(Handlers me) {
        me.add(new XssRequestExtendHandler());
        me.add(new ShiroHandler());
        //druid路由及认证
        me.add(new DruidStatViewExtendHandler("/Admin/Druid", request -> Start.JFniceDevMode || "1".equals(CurrentUser.getUserCode())));
    }

    // 系统启动完成后回调
    public void onStart() {
        StaticRoute.build(routesList);
        //清除日志缓存
        DictKit.clear();
    }

    // 系统关闭之前回调
    public void onStop() {
    }

}
