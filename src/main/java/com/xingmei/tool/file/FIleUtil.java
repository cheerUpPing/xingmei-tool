package com.xingmei.tool.file;

import java.io.File;

public class FIleUtil {

    /**
     * 获取文件拓展名
     *
     * @param file
     * @return
     */
    public static String getFileType(File file) {
        String fileName = file.getName();
        return FIleUtil.getFileType(fileName);
    }

    /**
     * 获取文件拓展名
     *
     * @param fileName
     * @return
     */
    public static String getFileType(String fileName) {
        if (!fileName.contains(".")) {
            return null;
        }
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        return fileType.equals("") ? null : fileType;
    }

    public static void main(String[] args) {
        File file = new File("D:\\test\\3ad1668da63c67a9dc421238938dfb8c.jpg");
        System.out.println("" + getFileType(file));
    }
}
