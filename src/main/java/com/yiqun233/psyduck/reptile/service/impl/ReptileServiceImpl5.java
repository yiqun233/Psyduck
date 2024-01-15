package com.yiqun233.psyduck.reptile.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yiqun233.psyduck.reptile.domain.TMetadataHtml;
import com.yiqun233.psyduck.reptile.domain.TMetadataHtmlChapters;
import com.yiqun233.psyduck.reptile.domain.TMetadataHtmlRes;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlChaptersMapper;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlMapper;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlResMapper;
import com.yiqun233.psyduck.reptile.service.ReptileService5;
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
import java.util.Objects;
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
public class ReptileServiceImpl5 implements ReptileService5 {


    @Resource
    TMetadataHtmlMapper tMetadataHtmlMapper;

    @Resource
    TMetadataHtmlResMapper tMetadataHtmlResMapper;

    @Resource
    TMetadataHtmlChaptersMapper tMetadataHtmlChaptersMapper;


    /**
     * 石油和天然气地质循环每一期每一篇文章概括
     *
     * @throws IOException
     */
    public void getChildAddress() throws IOException {
        //1349 41.1期   1371 44.5
        for (int i = 1349; i < 1372; i++) {
            String url = "http://ogg.pepris.com/CN/volumn/volumn_" + i + ".shtml";
            String htmlDocument = ReptileUtil.getHtmlDocument(url);
            Document doc = Jsoup.parse(htmlDocument);
            Elements select = doc.select("div.wenzhang");
            for (Element element : select) {
                Elements literatureMain = element.select("a.biaoti");
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
        String htmlDocument = ReptileUtil.getHtmlDocument(url);
        Document doc = Jsoup.parse(htmlDocument);
        if (doc.selectFirst("span.change-section") != null && doc.selectFirst("span.change-section").selectFirst("a") != null) {
            Elements selects = doc.select("span.change-section");
            for (Element select : selects) {
                Element label = Objects.requireNonNull(select).selectFirst("a");
                String onclick = null;
                if (label != null) {
                    onclick = label.attr("onclick");
                }
                if (!onclick.contains("RICH_HTML")) {
                    return;
                }
                String urlAttr = null;
                String year = null;
                if (onclick != null) {
                    urlAttr = ReptileUtil.extractStringBetweenLastTwoSingleQuotes(onclick);
                }
                String contentUrl = "http://ogg.pepris.com/" + urlAttr;
                try {
                    getLiteratureContent(contentUrl);
                } catch (Exception e) {
                    log.error("失败的地址" + contentUrl);
                }
            }

        }
    }


    public void getLiteratureContent(String url) throws IOException {
        String id = IdUtil.simpleUUID();
        String content = ReptileUtil.getHtmlDocument(url);
        Document doc = Jsoup.parse(content);

        //doi
        Element metaTag = doc.selectFirst("meta[name=citation_doi]");
        String doi = null;
        if (metaTag != null) {
            doi = metaTag.attr("content");
        }

        //中文
        Element cn = doc.selectFirst("div.title-cn");
        String titleCn = "";
        String authorsCn = "";
        String orgCn = "";
        //标题cn
        if (cn != null) {
            Element element1 = cn.selectFirst("article-title");
            if (element1 != null) {
                titleCn = element1.text();
            }
            //作者cn
            Element element3 = cn.selectFirst("p.author_cn");
            if (element3 != null) {
                authorsCn = element3.text();
            }
            Element element5 = cn.selectFirst("div.aff_cn");
            if (element5 != null) {
                orgCn = element5.text();
            }
        }


        //英文
        Element en = doc.selectFirst("div.title-en");
        String titleEn = "";
        String authorsEn = "";
        String orgEn = "";
        if (en != null) {
            //标题en
            Element element2 = en.selectFirst("trans-title");
            if (element2 != null) {
                titleEn = element2.text();
            }
            //作者en
            Element element4 = en.selectFirst("p.author_cn");
            if (element4 != null) {
                authorsEn = element4.text();
            }
            Element element6 = cn.selectFirst("div.aff_cn");
            if (element6 != null) {
                orgEn = element6.text();
            }
        }


        // 摘要cn
        Element abstractDivCn = doc.selectFirst("div.zhaiyao-cn-content");
        String keywordCn = "";
        String abstractCn = "";
        if (abstractDivCn != null) {
            Element cnAbstract = abstractDivCn.selectFirst("abstract");
            if (cnAbstract != null) {
                abstractCn = cnAbstract.text();
            }
            // 关键词cn
            Element cnKeyword = abstractDivCn.selectFirst("p.keyword_cn");
            if (cnKeyword != null) {
                keywordCn = cnKeyword.text();
            }
        }


        // 摘要en
        Element abstractDivEn = doc.selectFirst("div.zhaiyao-en-content");
        String abstractEn = "";
        String keywordEn = "";
        if (abstractDivEn != null) {
            Element enAbstract = abstractDivEn.selectFirst("trans-abstract");
            if (enAbstract != null) {
                abstractEn = enAbstract.text();
            }
            // 关键词en
            Element enKeyword = abstractDivEn.selectFirst("p.keyword_en");
            if (enKeyword != null) {
                keywordEn = enKeyword.text();
            }
        }


        //正文
        Element bodyAll = doc.selectFirst("div.content-zw");
        Elements divs = bodyAll.select("h1[id^=outline_anchor_],h2[id^=outline_anchor_],h3[id^=outline_anchor_],h4[id^=outline_anchor_],h5[id^=outline_anchor_]");
        for (Element level : divs) {
            String levelText = level.text();
            System.out.println(levelText);
        }
        List<String> collect = divs.stream().map(Element::text).filter(StrUtil::isNotEmpty).collect(Collectors.toList());
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
        Elements pictureAll = doc.select("div.content-zw-img");
        for (Element element : pictureAll) {
            if (element.selectFirst("p.tishi") != null && element.selectFirst("p.tishi").selectFirst("a") != null) {
                Element a = element.selectFirst("p.tishi").selectFirst("a");
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
                    Element shuoming = element.selectFirst("div.content-zw-img-shuoming");
                    Optional<TMetadataHtmlChapters> first = tMetadataHtmlChapters.stream().filter(i -> i.getContent().contains(shuoming.text())).findFirst();

                    int lastSlashIndex = url.lastIndexOf("/");
                    String result = url.substring(0, lastSlashIndex);
                    String picUrl = result + "/" + href;
                    String path = "\\pic\\石油与天然气地质\\" + pa;
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
                    String docContent = ReptileUtil.getHtmlDocument(formUrl);
                    Document docContentDocument = Jsoup.parse(docContent);
                    tMetadataHtmlRes.setTableHtml(docContentDocument.html());
                    tMetadataHtmlResMapper.insert(tMetadataHtmlRes);
                }
            }
        }

        TMetadataHtml metadataHtml = new TMetadataHtml();
        metadataHtml.setId(id);
        metadataHtml.setType(5);
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
}
