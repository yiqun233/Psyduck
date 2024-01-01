package com.yiqun233.psyduck.reptile;

import cn.hutool.core.util.IdUtil;
import com.yiqun233.psyduck.reptile.domain.TMetadataHtml;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlMapper;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlResMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * /**
 *
 * @author yiqun
 * @date 2023/12/29 10:58
 * @description
 */
public class StartReptile {

    @Resource
    TMetadataHtmlMapper  tMetadataHtmlMapper;

    @Resource
    TMetadataHtmlResMapper tMetadataHtmlResMapper;

    public void getContent() throws IOException {
        String url = "https://jns.nju.edu.cn/article/2019/0469-5097/0469-5097-2019-55-6-879.shtml"; // 替换为你想要爬取的URL

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

        // 使用Jsoup解析HTML内容
        Document doc = Jsoup.parse(content.toString());
        System.out.println("标题--》" + doc.title());

        // 使用CSS选择器获取meta标签
        Element metaTag = doc.selectFirst("meta[name=citation_doi]");
        // 获取content属性的值
        String doi = metaTag.attr("content");
        System.out.println("DOI: " + doi);

        // 使用CSS选择器获取meta标签
        Element metaTag2 = doc.selectFirst("meta[name=citation_authors]");
        // 获取content属性的值
        String authors = metaTag2.attr("content");
        System.out.println("authors: " + authors);


        // 使用CSS选择器获取div元素
        Element contentDiv = doc.selectFirst("div.zhaiyao-cn-content");
        // 获取div元素的文本内容
        String zhaiyao = contentDiv.text();
        System.out.println("摘要: " + zhaiyao);


        // 使用CSS选择器获取div元素
        Elements introductions = doc.selectFirst("div.content-zw-1").select("p");
        for (Element introduction : introductions) {
            // 获取div元素的文本内容
            String qianyan = introduction.text();
            System.out.println("前言: " + qianyan);
        }
        TMetadataHtml metadataHtml = new TMetadataHtml();
        metadataHtml.setId(IdUtil.simpleUUID());
        metadataHtml.setType(2);
        metadataHtml.setTitle(doc.title());
        metadataHtml.setAuthors(authors);
        metadataHtml.setDoi(doi);
        metadataHtml.setDigestCn(zhaiyao);
        tMetadataHtmlMapper.insert(metadataHtml);
    }



}
