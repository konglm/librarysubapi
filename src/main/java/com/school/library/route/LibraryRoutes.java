package com.school.library.route;

import com.jfnice.core.JFniceBaseRoutes;
import com.jfnice.interceptor.*;
import com.school.demo.IndexController;
import com.school.library.book.BookController;
import com.school.library.bookdamaged.BookDamagedController;
import com.school.library.bookinventory.BookInventoryController;
import com.school.library.bookstorage.BookStorageController;
import com.school.library.borrowbook.BorrowBookController;
import com.school.library.borrowsetting.BorrowSettingController;
import com.school.library.catalog.CatalogController;
import com.school.library.depositrecharge.DepositRechargeController;
import com.school.library.depositreturn.DepositReturnController;
import com.school.library.heartbeat.HeartBeatController;
import com.school.library.schupdate.UpgradeController;
import com.school.library.statistics.StatisticsController;
import com.school.library.userinfo.UserInfoController;

public class LibraryRoutes extends JFniceBaseRoutes {

    public LibraryRoutes() {
        setProjectName("Api");
        setProjectUrl("/api");
    }

    @Override
    public void config() {

        addInterceptor(new CrossInterceptor());// 允许跨域
        addInterceptor(new ApiExceptionInterceptor()); // 异常拦截
        addInterceptor(new ApiAuthInterceptor()); // 登陆拦截
        addInterceptor(new JsyShiroInterceptor()); // 授权认证
        addInterceptor(new ApiSignInterceptor()); // 验证sign
        addInterceptor(new XssInterceptor()); // XSS拦截
        addInterceptor(new InitUserInfoInterceptor()); // 初始化用户信息拦截器

        // 授权接口示例
        add("/api/borrowSetting", BorrowSettingController.class);
        add("/api/catalog", CatalogController.class);
        add("/api/bookStorage", BookStorageController.class);
        add("/api/userInfo", UserInfoController.class);
        add("/api/bookInventory", BookInventoryController.class);
        add("/api/statistics", StatisticsController.class);
        add("/api/borrowBook", BorrowBookController.class);
        add("/api/bookDamaged", BookDamagedController.class);
        add("/api/depositRecharge", DepositRechargeController.class);
        add("/api/book", BookController.class);
        add("/api/heartBeat", HeartBeatController.class);
        add("/api/Upgrade", UpgradeController.class); //年级升级
        add("/api/depositReturn", DepositReturnController.class);
    }

}

