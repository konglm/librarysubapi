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
        String projectName = "library"; //??????????????????????????????
        String moduleName = "??????????????????"; //????????????
        String tableName = "deposit_recharge"; //???????????????
        boolean isRelativeTable = false; //????????????????????????(???????????????????????? id, pid????????????)
        _CodeGen codeGenerator = new _CodeGen(projectName, moduleName, tableName, isRelativeTable);
        codeGenerator.generate();
    }

    private static DataSource getDataSource() {
        DruidPlugin druidPlugin = JFniceMainConfig.createDruidPlugin();
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }

    public void generate() {
        //genTable(); // ??????
        //genModel(); // ??????model
        genController();
        genValidator();
        genService();
        genLogic();
        //genSql();
        genLogicSql();
        genIdMap(); // ????????????

        //genCode(); // ??????????????????
        //genAcl(); // ????????????
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
        // base model ??????????????????
        String baseModelPackageName = "com.jfnice.model.base";
        // base model ??????????????????
        String baseModelOutputDir = PathKit.getWebRootPath() + "\\src\\main\\java\\com\\jfnice\\model\\base";
        // model ?????????????????? (MappingKit ?????????????????????)
        String modelPackageName = "com.jfnice.model";
        // model ?????????????????? (MappingKit ??? DataDictionary ????????????????????????)
        String modelOutputDir = baseModelOutputDir + "\\..";

        // ???????????????
        Generator gen = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
        // ?????????????????????
        gen.setDialect(new SqlServerDialect());
        // ??????????????????????????????
        for (String table : _ModelGen.excludedTable) {
            gen.addExcludedTable(table);
        }
        // ????????????setter ??? fastjson???????????????????????????????????? ???
        gen.setGenerateChainSetter(false);
        // ??????????????? Model ????????? dao ??????
        gen.setGenerateDaoInModel(true);
        // ??????????????????????????????
        gen.setGenerateDataDictionary(true);
        // ????????????????????????????????????????????????modelName??????????????? "osc_user"??????????????? "osc_"????????????model?????? "User"?????? OscUser
        // gernerator.setRemovedTableNamePrefixes("t_");
        // ??????
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
