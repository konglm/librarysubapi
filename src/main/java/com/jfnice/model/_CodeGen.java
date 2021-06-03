package com.jfnice.model;

import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.jfnice.core.JFniceMainConfig;
import org.apache.commons.io.FileUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class _CodeGen {

    private String projectName;
    private String moduleName;
    private String tableName;
    private boolean isRelativeTable;
    private String modelName;
    private String lowercaseModelName;
    private String firstCharLowerModelName;
    private Kv kv;
    private String tplDir;
    private String generateJavaRootPath;
    private Engine engine;

    public _CodeGen(String projectName, String moduleName, String tableName, boolean isRelativeTable) {
        this.projectName = projectName;
        this.moduleName = moduleName;
        this.tableName = tableName;
        this.isRelativeTable = isRelativeTable;
        this.modelName = StrKit.firstCharToUpperCase(StrKit.toCamelCase(tableName));
        this.lowercaseModelName = this.modelName.toLowerCase();
        this.firstCharLowerModelName = StrKit.firstCharToLowerCase(this.modelName);

        this.kv = Kv.by("projectName", this.projectName)
                .set("moduleName", this.moduleName)
                .set("tableName", this.tableName)
                .set("modelName", this.modelName)
                .set("lowercaseModelName", this.lowercaseModelName)
                .set("firstCharLowerModelName", this.firstCharLowerModelName);

        if (this.isRelativeTable) {
            this.tplDir = PathKit.getWebRootPath() + "\\src\\main\\webapp\\Admin\\_CodeGen\\relative\\";
        } else {
            this.tplDir = PathKit.getWebRootPath() + "\\src\\main\\webapp\\Admin\\_CodeGen\\common\\";
        }
        engine = Engine.create("CodeEngine");
        engine.setBaseTemplatePath(tplDir);
        this.generateJavaRootPath = PathKit.getWebRootPath() + "\\src\\main\\java\\com\\jfnice";
    }

    public static void main(String[] args) {
        String projectName = "library"; //路由定义的项目英文名
        String moduleName = "充值押金记录"; //模块名称
        String tableName = "deposit_recharge"; //数据库表名
        boolean isRelativeTable = false; //是否为层级关系表(层级关系表为包含 id, pid字段的表)
        _CodeGen codeGenerator = new _CodeGen(projectName, moduleName, tableName, isRelativeTable);
        codeGenerator.generate();
    }

    private static DataSource getDataSource() {
        DruidPlugin druidPlugin = JFniceMainConfig.createDruidPlugin();
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }

    public void generate() {
        //genTable(); // 建表
        //genModel(); // 创建model
        genController();
        genValidator();
        genService();
        genLogic();
        //genSql();
        genLogicSql();
        genIdMap(); // 创建缓存

        //genCode(); // 创建前台代码
        //genAcl(); // 创建权限
    }

    private void writeToFile(String content, String path, String fileName) throws IOException {
        File dir = new File(path);
        if (!dir.exists()) {
            FileUtils.forceMkdir(dir);
        }

        String target = path + fileName;
        System.out.println(target);
        if (new File(target).exists()) {
            return;
        }

        try (FileWriter fw = new FileWriter(target)) {
            fw.write(content);
        }
    }

    public void genTable() {
        System.out.println("------------- Create Table start ------------");
        Template template = engine.getTemplate("table.sql");
        String sql = template.renderToString(kv);
        try {
            Connection conn = getDataSource().getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("CREATE TABLE " + tableName);
        System.out.println("------------- Create Table end ------------\n");
    }

    public void genModel() {
        System.out.println("------------- Create Model start ------------");
        // base model 所使用的包名
        String baseModelPackageName = "com.jfnice.model.base";
        // base model 文件保存路径
        String baseModelOutputDir = PathKit.getWebRootPath() + "\\src\\main\\java\\com\\jfnice\\model\\base";
        // model 所使用的包名 (MappingKit 默认使用的包名)
        String modelPackageName = "com.jfnice.model";
        // model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
        String modelOutputDir = baseModelOutputDir + "\\..";

        // 创建生成器
        Generator gen = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
        // 设置数据库方言
        gen.setDialect(new SqlServerDialect());
        // 添加不需要生成的表名
        for (String table : _ModelGen.excludedTable) {
            gen.addExcludedTable(table);
        }
        // 生成链式setter 【 fastjson反序列有问题，不建议使用 】
        gen.setGenerateChainSetter(false);
        // 设置是否在 Model 中生成 dao 对象
        gen.setGenerateDaoInModel(true);
        // 设置是否生成字典文件
        gen.setGenerateDataDictionary(true);
        // 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
        // gernerator.setRemovedTableNamePrefixes("t_");
        // 生成
        gen.generate();
        System.out.println("------------- Create Model end ------------\n");
    }

    public void genSql() {
        System.out.println("------------- Create Sql start ------------");
        Template template = engine.getTemplate(".sql");
        String content = template.renderToString(kv);
        try {
            writeToFile(content, generateJavaRootPath + "\\_gen\\" + lowercaseModelName + "\\", modelName + ".sql");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("------------- Create Sql end ------------\n");
    }

    public void genLogicSql() {
        System.out.println("------------- Create LogicSql start ------------");
        Template template = engine.getTemplate("Logic.sql");
        String content = template.renderToString(kv);
        try {
            writeToFile(content, generateJavaRootPath + "\\_gen\\" + lowercaseModelName + "\\", modelName + "Logic.sql");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("------------- Create LogicSql end ------------\n");
    }

    public void genController() {
        System.out.println("------------- Create Controller start ------------");
        Template template = engine.getTemplate("Controller.java");
        String content = template.renderToString(kv);
        try {
            writeToFile(content, generateJavaRootPath + "\\_gen\\" + lowercaseModelName + "\\", modelName + "Controller.java");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("------------- Create Controller end ------------\n");
    }

    public void genValidator() {
        System.out.println("------------- Create Validator start ------------");
        Template template = engine.getTemplate("Validator.java");
        String content = template.renderToString(kv);
        try {
            writeToFile(content, generateJavaRootPath + "\\_gen\\" + lowercaseModelName + "\\", modelName + "Validator.java");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("------------- Create Validator end ------------\n");
    }

    public void genService() {
        System.out.println("------------- Create Service start ------------");
        Template template = engine.getTemplate("Service.java");
        String content = template.renderToString(kv);
        try {
            writeToFile(content, generateJavaRootPath + "\\_gen\\" + lowercaseModelName + "\\", modelName + "Service.java");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("------------- Create Service end ------------\n");
    }

    public void genLogic() {
        System.out.println("------------- Create Logic start ------------");
        Template template = engine.getTemplate("Logic.java");
        String content = template.renderToString(kv);
        try {
            writeToFile(content, generateJavaRootPath + "\\_gen\\" + lowercaseModelName + "\\", modelName + "Logic.java");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("------------- Create Logic end ------------\n");
    }

    public void genIdMap() {
        System.out.println("------------- Create IdMap start ------------");
        Template template = engine.getTemplate("IdMap.java");
        String content = template.renderToString(kv);
        try {
            writeToFile(content, generateJavaRootPath + "\\_gen\\" + lowercaseModelName + "\\", modelName + "IdMap.java");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("------------- Create IdMap end ------------\n");
    }

    public void genCode() {
        System.out.println("------------- Create Code start ------------");
        try {
            Template indexTemplate = engine.getTemplate("index.html");
            String indexContent = indexTemplate.renderToString(kv);
            writeToFile(indexContent, PathKit.getWebRootPath() + "\\Admin\\" + modelName + "\\", "index.html");

            Template addTemplate = engine.getTemplate("add.html");
            String addContent = addTemplate.renderToString(kv);
            writeToFile(addContent, PathKit.getWebRootPath() + "\\Admin\\" + modelName + "\\", "add.html");

            Template editTemplate = engine.getTemplate("edit.html");
            String editContent = editTemplate.renderToString(kv);
            writeToFile(editContent, PathKit.getWebRootPath() + "\\Admin\\" + modelName + "\\", "edit.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("------------- Create Code end ------------\n");
    }

}
