package com.yiqun233.psyduck.reptile.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiqun233.psyduck.reptile.domain.TMetadataHtml;
import com.yiqun233.psyduck.reptile.domain.TMetadataHtmlRes;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlMapper;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlResMapper;
import com.yiqun233.psyduck.reptile.service.TMetadataHtmlService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
import java.util.Objects;

/**
 * @author YI
 * @description 针对表【t_metadata_html】的数据库操作Service实现
 * @createDate 2023-12-29 13:07:02
 */
@Service
public class TMetadataHtmlServiceImpl extends ServiceImpl<TMetadataHtmlMapper, TMetadataHtml>
        implements TMetadataHtmlService {


    @Resource
    TMetadataHtmlMapper tMetadataHtmlMapper;

    @Resource
    TMetadataHtmlResMapper tMetadataHtmlResMapper;

    public String getHtmlDocument(String url) throws IOException {
        String result = "";
        // 发送HTTP GET请求
        URLConnection connection = new URL(url).openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        httpConn.setRequestMethod("GET");
        httpConn.connect();

        // 检查连接是否成功
        int responseCode = httpConn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed to connect: " + responseCode);
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

    /**
     * 南京大学循环每一期每一篇文章概括
     *
     * @throws IOException
     */
    public void getChildAddress() throws IOException {
        for (int i = 70; i < 88; i++) {
            String url = "https://jns.nju.edu.cn/CN/volumn/volumn_" + i + ".shtml";
            String htmlDocument = getHtmlDocument(url);
            Document doc = Jsoup.parse(htmlDocument);
            Elements select = doc.select("dd.list-dd");
            for (Element element : select) {
                Elements literatureMain = element.select("li.biaoti").select("a");
                String literatureMainUrl = literatureMain.attr("href");
                getLiteratureHtmlAddress(literatureMainUrl);
            }
        }
    }


    /**
     * 南京大学 文章详情html
     *
     * @throws IOException
     */
    public void getLiteratureHtmlAddress(String url) throws IOException {
        String htmlDocument = getHtmlDocument(url);
        Document doc = Jsoup.parse(htmlDocument);
        if(doc.selectFirst("span.change-section") !=null &&doc.selectFirst("span.change-section").selectFirst("a")!=null ){
            Element label = Objects.requireNonNull(doc.selectFirst("span.change-section")).selectFirst("a");
            String onclick = null;
            if (label != null) {
                onclick = label.attr("onclick");
            }
            String urlAttr = null;
            String year = null;
            if (onclick != null) {
                urlAttr = extractStringBetweenLastTwoSingleQuotes(onclick);
            }
            String contentUrl = "https://jns.nju.edu.cn/" + urlAttr;
            getLiteratureContent(contentUrl);
        }
    }

    private static String extractStringBetweenLastTwoSingleQuotes(String input) {
        int lastQuoteIndex = input.lastIndexOf("'");
        int secondLastQuoteIndex = input.lastIndexOf("'", lastQuoteIndex - 1);

        if (lastQuoteIndex != -1 && secondLastQuoteIndex != -1) {
            return input.substring(secondLastQuoteIndex + 1, lastQuoteIndex);
        } else {
            // 如果没有找到两个单引号，返回空字符串或者抛出异常，取决于你的需求
            return "";
        }
    }

    private static String extractStringBetweenLastThreeAndFourSingleQuotes(String input) {

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

    private static String removeHtmlExtension(String input) {
        // 检查字符串是否以 ".html" 结尾
        if (input.endsWith(".html")) {
            // 使用 substring 截取字符串，去除末尾的 ".html"
            return input.substring(0, input.length() - ".html".length());
        } else {
            // 如果不是以 ".html" 结尾，返回原始字符串或者抛出异常，取决于你的需求
            return input;
        }
    }

    private static String saveImageToLocal(String imageUrl, String localFolderPath) throws IOException {
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

    private static void createFolderIfNotExists(String folderPath) throws IOException {
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

    public void getLiteratureContent(String url) throws IOException {
        String id = IdUtil.simpleUUID();
//        String url = "https://jns.nju.edu.cn/article/2019/0469-5097/0469-5097-2019-55-6-879.shtml"; // 替换为你想要爬取的URL

        String content = getHtmlDocument(url);

        //标题
        Document doc = Jsoup.parse(content);
        String title = doc.title();

        //doi
        Element metaTag = doc.selectFirst("meta[name=citation_doi]");
        String doi = null;
        if (metaTag != null) {
            doi = metaTag.attr("content");
        }

        //作者
        Element metaTag2 = doc.selectFirst("meta[name=citation_authors]");
        String authors = null;
        if (metaTag2 != null) {
            authors = metaTag2.attr("content");
        }


        // 摘要cn
        Element contentDivCn = doc.selectFirst("div.zhaiyao-cn-content");
        String zhaiyaoCn = null;
        if (contentDivCn != null) {
            zhaiyaoCn = contentDivCn.text();
        }

        // 摘要en
        Element contentDivEn = doc.selectFirst("div.zhaiyao-en-content");
        String zhaiyaoEn = null;
        if (contentDivEn != null) {
            zhaiyaoEn = contentDivEn.text();
        }

        //正文
        Elements bodyAll = doc.select("div.content-zw-1");
        StringBuilder mainBody = new StringBuilder();
        for (Element element : bodyAll) {
            mainBody.append(element.text());
        }

        //图片
        Elements pictureAll = doc.select("div.content-zw-img");
        for (Element element : pictureAll) {
            if (element.selectFirst("p.tishi") != null && element.selectFirst("p.tishi").selectFirst("a") != null) {
                Element a = element.selectFirst("p.tishi").selectFirst("a");
                String href = null;
                if (a != null) {
                    href = a.attr("href");
                }
                if (href != null) {
                    href = removeHtmlExtension(href);

                    int lastSlashIndex = url.lastIndexOf("/");
                    String result = url.substring(0, lastSlashIndex);
                    String picUrl = result + "/" + href;
                    String path = "\\pic\\南京大学\\" + title;
                    String pathName = saveImageToLocal(picUrl, path);

                    TMetadataHtmlRes tMetadataHtmlRes = new TMetadataHtmlRes();
                    tMetadataHtmlRes.setId(IdUtil.simpleUUID());
                    tMetadataHtmlRes.setMetadataHtmlId(id);
                    tMetadataHtmlRes.setType(1);
                    tMetadataHtmlRes.setPath(pathName);
                    tMetadataHtmlResMapper.insert(tMetadataHtmlRes);
                }

            }
        }

        //表格
        Elements formAll = doc.select("div.zw-zsbg");
        for (Element element : formAll) {
            if (element.selectFirst("p.biaotishi1") != null && element.selectFirst("p.biaotishi1").selectFirst("a") != null) {
                Element a = element.selectFirst("p.biaotishi1").selectFirst("a");
                String href = null;
                if (a != null) {
                    href = a.attr("href");
                    int lastSlashIndex = url.lastIndexOf("/");
                    String result = url.substring(0, lastSlashIndex);
                    String formUrl = result + "/" + href;
                    TMetadataHtmlRes tMetadataHtmlRes = new TMetadataHtmlRes();
                    tMetadataHtmlRes.setId(IdUtil.simpleUUID());
                    tMetadataHtmlRes.setMetadataHtmlId(id);
                    tMetadataHtmlRes.setType(2);
                    tMetadataHtmlRes.setTableHtml(formUrl);
                    tMetadataHtmlResMapper.insert(tMetadataHtmlRes);
                }
            }

        }

        TMetadataHtml metadataHtml = new TMetadataHtml();
        metadataHtml.setId(id);
        metadataHtml.setType(2);
        metadataHtml.setTitle(title);
        metadataHtml.setAuthors(authors);
        metadataHtml.setDoi(doi);
        metadataHtml.setDigestCn(zhaiyaoCn);
        metadataHtml.setDigestEn(zhaiyaoEn);
        metadataHtml.setMainBody(mainBody.toString());
        metadataHtml.setMetadataStr(content);
        tMetadataHtmlMapper.insert(metadataHtml);
    }

}




