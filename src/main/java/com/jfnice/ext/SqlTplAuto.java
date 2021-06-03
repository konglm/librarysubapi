package com.jfnice.ext;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 * 如果采用"src/sql"这种存放方式，并且该文件夹下面没有java可编译文件，
 * 那么编译后该文件夹下面的所有文件会被存放到classes目录下面(类似resource文件夹)，
 * 表现为jetty开发环境正常，但打包成war包会产生重复sql文件，以致arp.addSqlTemplate新增多份重复文件，
 * 导致部署到tomcat等容器时不能正常启动！不建议使用"src/sql"这种存放方式，建议存放到带有java文件的包里。
 */
public class SqlTplAuto {

    private static String sqlTplExt = "sql";

    public static void addAllSqlTemplate(ActiveRecordPlugin arp, String rootPath) {
        List<File> fileList = (List<File>) FileUtils.listFiles(new File(rootPath), new String[]{sqlTplExt}, true);
        fileList.forEach(e -> arp.addSqlTemplate(e.getAbsolutePath().replace(rootPath, "")));
    }

}
