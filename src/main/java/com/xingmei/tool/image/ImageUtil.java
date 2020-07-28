package com.xingmei.tool.image;

import com.xingmei.tool.file.FileUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageUtil {

    /**
     * 图片base64编码
     *
     * @param imageFilePath
     * @return
     * @throws IOException
     */
    public static String imageBase64Encode(String imageFilePath) throws IOException {
        File file = new File(imageFilePath);
        return ImageUtil.imageBase64Encode(file);
    }

    /**
     * 图片base64编码
     *
     * @param imageFile
     * @return
     * @throws IOException
     */
    public static String imageBase64Encode(File imageFile) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, FileUtil.getFileType(imageFile), byteArrayOutputStream);
        return Base64.encodeBase64String(byteArrayOutputStream.toByteArray());
    }

    /**
     * 图片64字符串解码
     *
     * @param imageBase64
     * @param fileAllPath
     * @return
     * @throws IOException
     */
    public static File imageBase64Decode(String imageBase64, String fileAllPath) throws IOException {
        byte[] imageBytes = Base64.decodeBase64(imageBase64);
        File file = new File(fileAllPath);
        FileUtils.writeByteArrayToFile(file, imageBytes);
        return file;
    }

    public static void main(String[] args) throws IOException {
        List<File> fileList = new ArrayList<>();
        Map<String, File> fileMap = new HashMap<>();
        if (fileMap.containsKey(null)){
            System.out.println("==========");
        }
        for (File file1 : fileList){
            System.out.println(file1.getAbsoluteFile());
        }
    }

}
