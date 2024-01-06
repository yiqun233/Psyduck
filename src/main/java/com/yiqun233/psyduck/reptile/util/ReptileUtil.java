package com.yiqun233.psyduck.reptile.util;

import cn.hutool.core.util.IdUtil;
import com.yiqun233.psyduck.reptile.domain.TMetadataHtmlChapters;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * /**
 *
 * @author Qun Q Yi
 * @date 2024/1/4 18:02
 * @description
 */
public class ReptileUtil {

    public static String getHtmlDocument(String url) throws IOException {
        String result = "";
        // 发送HTTP GET请求
        URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        httpConn.setRequestMethod("GET");
        httpConn.connect();

        // 检查连接是否成功
        int responseCode = httpConn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed to connect: " +url + responseCode);
        }

        // 读取响应内容
        BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        result = String.valueOf(content);
        return result;
    }

    public static String extractStringBetweenLastTwoSingleQuotes(String input) {
        int lastQuoteIndex = input.lastIndexOf("'");
        int secondLastQuoteIndex = input.lastIndexOf("'", lastQuoteIndex - 1);

        if (lastQuoteIndex != -1 && secondLastQuoteIndex != -1) {
            return input.substring(secondLastQuoteIndex + 1, lastQuoteIndex);
        } else {
            // 如果没有找到两个单引号，返回空字符串或者抛出异常，取决于你的需求
            return "";
        }
    }

    public static String extractStringBetweenLastThreeAndFourSingleQuotes(String input) {

        int lastQuoteIndex = input.lastIndexOf("'");
        int secondLastQuoteIndex = input.lastIndexOf("'", lastQuoteIndex - 1);
        // 找到倒数第三个单引号的位置
        int thirdLastQuoteIndex = input.lastIndexOf("'", secondLastQuoteIndex - 1);

        // 找到倒数第四个单引号的位置
        int fourthLastQuoteIndex = input.lastIndexOf("'", thirdLastQuoteIndex - 1);

        if (fourthLastQuoteIndex != -1 && thirdLastQuoteIndex != -1) {
            return input.substring(fourthLastQuoteIndex + 1, thirdLastQuoteIndex);
        } else {
            // 如果没有找到两个单引号，返回空字符串或者抛出异常，取决于你的需求
            return "";
        }
    }

    public static String removeHtmlExtension(String input) {
        // 检查字符串是否以 ".html" 结尾
        if (input.endsWith(".html")) {
            // 使用 substring 截取字符串，去除末尾的 ".html"
            return input.substring(0, input.length() - ".html".length());
        } else {
            // 如果不是以 ".html" 结尾，返回原始字符串或者抛出异常，取决于你的需求
            return input;
        }
    }

    public static String saveImageToLocal(String imageUrl, String localFolderPath) throws IOException {
        URL url = new URL(imageUrl);
        localFolderPath = sanitizePath(localFolderPath);
        createFolderIfNotExists(localFolderPath);
        // 使用 InputStream 从网络获取数据
        String fileName = "";
        try (InputStream in = url.openStream()) {
            // 构建本地文件路径
            fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path localPath = Paths.get(localFolderPath, fileName);

            // 创建目标文件
            Files.createFile(localPath);

            // 使用 FileOutputStream 将 InputStream 写入本地文件
            try (FileOutputStream out = new FileOutputStream(localPath.toFile())) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        }
        return localFolderPath + "/" + fileName;
    }

    public static void createFolderIfNotExists(String folderPath) throws IOException {
        Path path = Paths.get(folderPath);

        // 检查文件夹是否存在
        if (!Files.exists(path)) {
            // 如果不存在，创建文件夹
            Files.createDirectories(path);
        }
    }

    public static String sanitizePath(String originalPath) throws IOException {
        // 定义非法字符
        String illegalChars = "[<>:\"/\\|?*]";

        // 替换非法字符为下划线
        return originalPath.replaceAll(illegalChars, "_");
    }


    public static List<TMetadataHtmlChapters> processList(List<String> inputList, String mainId) {
        List<TMetadataHtmlChapters> resultList = new ArrayList<>();
        int seq = 1;
        String field1 = "";
        String field2 = "";
        String field3 = "";

        for (String string : inputList) {
            int index = string.indexOf(" ");
            String number = "";
            String context = "";
            if (index != -1) {
                number = string.substring(0, index);
                context = string.substring(index + 1);
            } else {
                break;
            }
            String[] split = number.split("\\.");
            if (split.length == 1) {
                field1 = string;
                field2 = "";
                field3 = "";
            } else if (split.length == 2) {
                field2 = string;
                field3 = "";
            } else if (split.length == 3) {
                field3 = string;
            }

            String[] parts = string.split("\\s+");
            TMetadataHtmlChapters tMetadataHtmlChapters = new TMetadataHtmlChapters();
            tMetadataHtmlChapters.setId(IdUtil.simpleUUID());
            tMetadataHtmlChapters.setMetadataHtmlId(mainId);
            tMetadataHtmlChapters.setChapterTitle1(field1);
            tMetadataHtmlChapters.setChapterTitle2(field2);
            tMetadataHtmlChapters.setChapterTitle3(field3);
            tMetadataHtmlChapters.setSequence(seq);
            tMetadataHtmlChapters.setAttr(string);
            resultList.add(tMetadataHtmlChapters);
            seq++;
        }
        return resultList;
    }
}
