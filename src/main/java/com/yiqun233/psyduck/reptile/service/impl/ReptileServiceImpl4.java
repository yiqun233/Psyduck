package com.yiqun233.psyduck.reptile.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yiqun233.psyduck.reptile.domain.TMetadataHtml;
import com.yiqun233.psyduck.reptile.domain.TMetadataHtmlChapters;
import com.yiqun233.psyduck.reptile.domain.TMetadataHtmlRes;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlChaptersMapper;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlMapper;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlResMapper;
import com.yiqun233.psyduck.reptile.service.ReptileService4;
import com.yiqun233.psyduck.reptile.util.ReptileUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * /**
 * 1沉积学报 2南京大学报 3海洋石油 4石油勘探与开发 5石油与天然气地质 6西南石油大学学报
 *
 * @author Qun Q Yi
 * @date 2024/1/15 16:46
 * @description
 */
@Slf4j
@Service
public class ReptileServiceImpl4 implements ReptileService4 {


    @Resource
    TMetadataHtmlMapper tMetadataHtmlMapper;

    @Resource
    TMetadataHtmlResMapper tMetadataHtmlResMapper;

    @Resource
    TMetadataHtmlChaptersMapper tMetadataHtmlChaptersMapper;


    /**
     * 石油勘探与开发
     *
     * @throws IOException
     */
    public void getChildAddress() throws IOException {
        // 1254 45.1期  1469 49.2 期
        for (int i = 1403; i < 1404; i++) {
            String url = "http://www.cpedm.com/CN/volumn/volumn_" + i + ".shtml";
            String htmlDocument = ReptileUtil.getHtmlDocument(url);
            Document doc = Jsoup.parse(htmlDocument);
            Elements selects = doc.select("div.article-l");

            for (Element select : selects) {
                Element literatureMain = select.selectFirst("div.j-btn").selectFirst("span.richhtml").selectFirst("a");
                if (literatureMain != null) {
                    String literatureMainUrl = literatureMain.attr("href");
                    String doi = select.selectFirst("a.j-doi").text();
                    String removre = "https://doi.org/";
                    doi = doi.substring(removre.length());
                    try {
                        getLiteratureContent(literatureMainUrl, doi);
                    } catch (Exception e) {
                        System.out.println("错误地址" + literatureMainUrl);
                    }

                }
            }
        }
    }

    public void getLiteratureContent(String url, String doi) throws IOException {
        String id = IdUtil.simpleUUID();
        String content = ReptileUtil.getHtmlDocument(url);
        Document doc = Jsoup.parse(content);

        //中文
        Element element1 = doc.selectFirst("div.article_title_cn");
        String titleCn = "";
        if (element1 != null) {
            titleCn = element1.text();
        }
        //英文
        Element element2 = doc.selectFirst("div.article_title");
        String titleEn = "";
        if (element2 != null) {
            titleEn = element2.text();
        }

        Elements element3 = doc.select("div.author_name_list");
        String authorsCn = "";
        String authorsEn = "";
        if (element3.size() > 1) {
            authorsCn = element3.get(0).text();
        }
        if (element3.size() > 1) {
            authorsEn = element3.get(1).text();
        }
        if (element3.size() == 1) {
            authorsEn = element3.get(0).text();
            authorsCn = element3.get(0).text();
        }

        Elements element4 = doc.select("abstract");
        String abstractCn = "";
        String abstractEn = "";
        if (element4.size() > 1) {
            abstractCn = element4.get(0).text();
        }
        if (element4.size() > 1) {
            abstractEn = element4.get(1).text();
        }
        if (element4.size() == 1) {
            abstractEn = element4.get(0).text();
            abstractCn = element4.get(0).text();
        }

        Elements element5 = doc.select("div.key");
        String keywordCn = "";
        String keywordEn = "";
        if (element5.size() > 1) {
            keywordCn = element5.get(0).text();
        }
        if (element5.size() > 1) {
            keywordEn = element5.get(1).text();
        }
        if (element5.size() == 1) {
            keywordEn = element5.get(0).text();
            keywordCn = element5.get(0).text();
        }

        Elements element6 = doc.select("div.affiliation_list");
        String orgCn = "";
        String orgEn = "";
        if (element6.size() > 1) {
            orgCn = element6.get(0).text();
        }
        if (element6.size() > 1) {
            orgEn = element6.get(1).text();
        }
        if (element6.size() == 1) {
            orgEn = element6.get(0).text();
            orgCn = element6.get(0).text();
        }



        //正文
        Element bodyAll = doc.selectFirst("div.article_body");
        Elements divs = bodyAll.select("span.paragraph_title");
        for (Element level : divs) {
            String levelText = level.text();
            System.out.println(levelText);
        }
        List<String> collect = divs.stream().map(Element::text).collect(Collectors.toList());
        List<TMetadataHtmlChapters> tMetadataHtmlChapters = ReptileUtil.processList(collect, id);
        String text = bodyAll.text();
        for (int i = 0; i < tMetadataHtmlChapters.size(); i++) {
            TMetadataHtmlChapters chapters = tMetadataHtmlChapters.get(i);
            if (i != tMetadataHtmlChapters.size() - 1) {
                int beginIndex = text.indexOf(tMetadataHtmlChapters.get(i).getAttr());
                int lastIndex = text.indexOf(tMetadataHtmlChapters.get(i + 1).getAttr());
                chapters.setContent(text.substring(beginIndex, lastIndex));
                tMetadataHtmlChaptersMapper.insert(chapters);
            } else {
                int beginIndex = text.indexOf(tMetadataHtmlChapters.get(i).getAttr());
                int lastIndex = text.indexOf("参考文件");
                chapters.setContent(text.substring(beginIndex));
                tMetadataHtmlChaptersMapper.insert(chapters);
            }
        }

        //图片
        Elements pictureAll = doc.select("div.figure.outline_anchor");
        for (Element element : pictureAll) {
            try {
                Element a = element.selectFirst("a.group3");

                String href = null;

                if (a != null) {
                    href = a.attr("href");
                }
                if (href != null) {
                    String pa = "";
                    if (StrUtil.isNotEmpty(titleCn)) {
                        pa = titleCn;
                    } else if (StrUtil.isEmpty(titleCn) && StrUtil.isNotEmpty(titleEn)) {
                        pa = titleEn;
                    } else {
                        continue;
                    }
                    href = ReptileUtil.removeHtmlExtension(href);
                    Element b = element.selectFirst("b");
                    Optional<TMetadataHtmlChapters> first = tMetadataHtmlChapters.stream().filter(i -> i.getContent().contains(b.text())).findFirst();

                    int lastSlashIndex = url.lastIndexOf("/");
                    String result = url.substring(0, lastSlashIndex);
                    String picUrl = result + "/" + href;
                    String path = "\\pic\\石油勘探与开发\\" + pa;
                    String pathName = ReptileUtil.saveImageToLocal(picUrl, path);

                    TMetadataHtmlRes tMetadataHtmlRes = new TMetadataHtmlRes();
                    tMetadataHtmlRes.setId(IdUtil.simpleUUID());
                    tMetadataHtmlRes.setMetadataHtmlId(id);
                    tMetadataHtmlRes.setType(1);
                    tMetadataHtmlRes.setPath(pathName);
                    if (first.isPresent()) {
                        tMetadataHtmlRes.setChapterId(first.get().getId());
                    }
                    tMetadataHtmlResMapper.insert(tMetadataHtmlRes);

                }
            } catch (Exception e) {

            }

        }

        //表格
        Elements formAll = doc.select("div.table.outline_anchor");
        for (Element element : formAll) {
            if (element != null) {
                TMetadataHtmlRes tMetadataHtmlRes = new TMetadataHtmlRes();
                tMetadataHtmlRes.setId(IdUtil.simpleUUID());
                tMetadataHtmlRes.setMetadataHtmlId(id);
                tMetadataHtmlRes.setType(2);
                Element b = element.selectFirst("b");
                Optional<TMetadataHtmlChapters> first = tMetadataHtmlChapters.stream().filter(i -> i.getContent().contains(b.text())).findFirst();
                tMetadataHtmlRes.setChapterId(first.get().getId());
                tMetadataHtmlRes.setTableHtml(element.html());
                tMetadataHtmlResMapper.insert(tMetadataHtmlRes);
            }
        }

        TMetadataHtml metadataHtml = new TMetadataHtml();
        metadataHtml.setId(id);
        metadataHtml.setType(4);
        metadataHtml.setDoi(doi);
        metadataHtml.setTitleCn(titleCn);
        metadataHtml.setTitleEn(titleEn);
        metadataHtml.setAuthorsCn(authorsCn);
        metadataHtml.setAuthorsEn(authorsEn);
        metadataHtml.setDigestCn(abstractCn);
        metadataHtml.setDigestEn(abstractEn);
        metadataHtml.setKeywordsCn(keywordCn);
        metadataHtml.setKeywordsEn(keywordEn);
        metadataHtml.setOrganizationCn(orgCn);
        metadataHtml.setOrganizationEn(orgEn);
        metadataHtml.setSource(url);
        metadataHtml.setMetadataStr(content);
        tMetadataHtmlMapper.insert(metadataHtml);
    }


    /**
     * 石油天然气与地址循环每一期每一篇文章概括
     *
     * @throws IOException
     */
    public void getChildAddress2() throws IOException {
        //1480 49.3期  1488 50.5期
        for (int i = 1480; i < 1489; i++) {
            String url = "http://www.cpedm.com/CN/volumn/volumn_" + i + ".shtml";
            String htmlDocument = ReptileUtil.getHtmlDocument(url);
            Document doc = Jsoup.parse(htmlDocument);
            Elements selects = doc.select("div.article-l");
            for (Element select : selects) {
                Element literatureMain = select.selectFirst("div.j-title-1").selectFirst("a");
                if (literatureMain != null) {
                    String literatureMainUrl = literatureMain.attr("href");
                    if (StrUtil.isNotEmpty(literatureMainUrl)) {
                        String doi = select.selectFirst("a.j-doi").text();
                        String removre = "https://doi.org/";
                        doi = doi.substring(removre.length());
                        try {
                            getLiteratureContent2(literatureMainUrl, doi);
                        } catch (Exception e) {
                            System.out.println(literatureMainUrl);
                        }
                    }
                }
            }
        }
    }

    public void getLiteratureContent2(String url, String doi) throws IOException {
        String id = IdUtil.simpleUUID();
        String content = ReptileUtil.getContentBySeleniumhq(url);
        Document doc = Jsoup.parse(content);

        //中文
        Elements element1 = doc.select("p.main_content_top_title");
        String titleCn = "";
        String titleEn = "";
        if (element1.size() > 1) {
            titleCn = element1.get(0).text();
        }
        if (element1.size() > 1) {
            titleEn = element1.get(1).text();
        }
        if (element1.size() == 1) {
            titleEn = element1.get(0).text();
        }

        Elements element3 = doc.select("div.main_content_top_btn");
        String authorsCn = "";
        String authorsEn = "";
        if (element3.size() > 2) {
            authorsCn = element3.get(1).text();
        }
        if (element3.size() > 2) {
            authorsEn = element3.get(2).text();
        }
        if (element3.size() == 2) {
            authorsEn = element3.get(1).text();
            authorsCn = element3.get(1).text();
        }

        Elements element4 = doc.select("div.main_content_center_left_zhengwen");
        String abstractCn = null;
        String abstractEn = null;
        if (element4.size() > 1) {
            abstractCn = element4.get(0).text();
        }
        if (element4.size() > 1) {
            abstractEn = element4.get(1).text();
        }
        if (element4.size() == 1) {
            abstractEn = element4.get(0).text();
        }

        Elements element5 = doc.select("p.main_content_center_left_zhengwen");
        String keywordCn = null;
        String keywordEn = null;
        if (element5.size() > 1) {
            keywordCn = element5.get(0).text();
        }
        if (element5.size() > 1) {
            keywordEn = element5.get(1).text();
        }
        if (element5.size() == 1) {
            keywordEn = element5.get(0).text();
        }

        //正文
        Element bodyAll = doc.getElementById("bodyVue");
        Elements divs = bodyAll.select("span.body_sec_title");
        for (Element level : divs) {
            String levelText = level.text();
            System.out.println(levelText);
        }
        List<String> collect = divs.stream().map(Element::text).collect(Collectors.toList());
        List<TMetadataHtmlChapters> tMetadataHtmlChapters = ReptileUtil.processList(collect, id);
        String text = bodyAll.text();
        for (int i = 0; i < tMetadataHtmlChapters.size(); i++) {
            TMetadataHtmlChapters chapters = tMetadataHtmlChapters.get(i);
            if (i != tMetadataHtmlChapters.size() - 1) {
                int beginIndex = text.indexOf(tMetadataHtmlChapters.get(i).getAttr());
                int lastIndex = text.indexOf(tMetadataHtmlChapters.get(i + 1).getAttr());
                chapters.setContent(text.substring(beginIndex, lastIndex));
                tMetadataHtmlChaptersMapper.insert(chapters);
            } else {
                int beginIndex = text.indexOf(tMetadataHtmlChapters.get(i).getAttr());
                int lastIndex = text.indexOf("参考文件");
                chapters.setContent(text.substring(beginIndex));
                tMetadataHtmlChaptersMapper.insert(chapters);
            }
        }

        //图片
        Elements pictureAll = doc.select("div.mag_main_zhengwen_left_div_p.mag_rich_body_p");
        for (Element element : pictureAll) {
            Element a = element.selectFirst("img");

            String href = null;

            if (a != null) {
                href = a.attr("src");
            }
            if (href != null) {
                String pa;
                if (StrUtil.isNotEmpty(titleCn)) {
                    pa = titleCn;
                } else if (StrUtil.isEmpty(titleCn) && StrUtil.isNotEmpty(titleEn)) {
                    pa = titleEn;
                } else {
                    continue;
                }
                href = ReptileUtil.removeHtmlExtension(href);
                Element b = element.selectFirst("strong");
                Optional<TMetadataHtmlChapters> first = tMetadataHtmlChapters.stream().filter(i -> i.getContent().contains(b.text())).findFirst();

                String picUrl = href;
                String path = "\\pic2\\石油勘探与开发\\" + pa;
                String pathName = ReptileUtil.saveImageToLocal(picUrl, path);

                TMetadataHtmlRes tMetadataHtmlRes = new TMetadataHtmlRes();
                tMetadataHtmlRes.setId(IdUtil.simpleUUID());
                tMetadataHtmlRes.setMetadataHtmlId(id);
                tMetadataHtmlRes.setType(1);
                tMetadataHtmlRes.setPath(pathName);
                if (first.isPresent()) {
                    tMetadataHtmlRes.setChapterId(first.get().getId());
                }
                tMetadataHtmlResMapper.insert(tMetadataHtmlRes);

            }
        }

        //表格
        Elements formAll = doc.select("div.mag_main_table");
        for (Element element : formAll) {
            if (element != null) {
                TMetadataHtmlRes tMetadataHtmlRes = new TMetadataHtmlRes();
                tMetadataHtmlRes.setId(IdUtil.simpleUUID());
                tMetadataHtmlRes.setMetadataHtmlId(id);
                tMetadataHtmlRes.setType(2);
                Element b = element.selectFirst("div.mag_main_table_title");
                Optional<TMetadataHtmlChapters> first = tMetadataHtmlChapters.stream().filter(i -> i.getContent().contains(b.text())).findFirst();
                tMetadataHtmlRes.setChapterId(first.get().getId());
                tMetadataHtmlRes.setTableHtml(element.html());
                tMetadataHtmlResMapper.insert(tMetadataHtmlRes);
            }
        }

        TMetadataHtml metadataHtml = new TMetadataHtml();
        metadataHtml.setId(id);
        metadataHtml.setType(4);
        metadataHtml.setDoi(doi);
        metadataHtml.setTitleCn(titleCn);
        metadataHtml.setTitleEn(titleEn);
        metadataHtml.setAuthorsCn(authorsCn);
        metadataHtml.setAuthorsEn(authorsEn);
        metadataHtml.setDigestCn(abstractCn);
        metadataHtml.setDigestEn(abstractEn);
        metadataHtml.setKeywordsCn(keywordCn);
        metadataHtml.setKeywordsEn(keywordEn);
        metadataHtml.setMetadataStr(content);
        tMetadataHtmlMapper.insert(metadataHtml);
    }
}
