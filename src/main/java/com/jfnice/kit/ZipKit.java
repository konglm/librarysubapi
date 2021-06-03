package com.jfnice.kit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.*;

/**
 * 压缩辅助类，暂不用到
 */
public class ZipKit {

    public static void zip(String path, String zip) {
        zip(path, zip, Deflater.BEST_COMPRESSION);
    }

    public static void zip(String path, String zip, int level) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        FileOutputStream fos = null;
        CheckedOutputStream csum = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zip);
            csum = new CheckedOutputStream(fos, new CRC32());
            zos = new ZipOutputStream(csum);
            zos.setLevel(level);
            zip(zos, file, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (csum != null) {
                    csum.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void zip(ZipOutputStream zos, File file, String base) {
        if (file.isFile()) {
            FileInputStream fis = null;
            try {
                String name = file.getName();
                if (base != null && !"".equals(base)) {
                    name = base + File.separator + file.getName();
                }
                zos.putNextEntry(new ZipEntry(name));
                fis = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, len);
                }
                zos.flush();
                zos.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (file.isDirectory()) {
            File[] fList = file.listFiles();
            if (fList.length == 0) {
                try {
                    zos.putNextEntry(new ZipEntry(base + File.separator));
                    zos.flush();
                    zos.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                for (File f : file.listFiles()) {
                    String b = null;
                    try {
                        if (base != null && !"".equals(base)) {
                            b = base + File.separator + f.getCanonicalPath().substring(f.getCanonicalPath().lastIndexOf(File.separator) + 1);
                        } else {
                            b = f.getCanonicalPath().substring(f.getCanonicalPath().lastIndexOf(File.separator) + 1);
                        }

                        if (f.isFile()) {
                            b = b.substring(0, b.length() - f.getName().length());
                            if (!"".equals(b)) {
                                b = b.substring(0, b.length() - 1);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    zip(zos, f, b);
                }
            }
        }
    }

}
