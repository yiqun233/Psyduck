package com.yiqun233.psyduck.reptile.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiqun233.psyduck.reptile.domain.TMetadataHtml;
import com.yiqun233.psyduck.reptile.domain.TMetadataHtmlChapters;
import com.yiqun233.psyduck.reptile.domain.TMetadataHtmlRes;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlChaptersMapper;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlMapper;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlResMapper;
import com.yiqun233.psyduck.reptile.service.TMetadataHtmlService;
import com.yiqun233.psyduck.reptile.util.ReptileUtil;
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

    @Resource
    TMetadataHtmlChaptersMapper tMetadataHtmlChaptersMapper;


    /**
     * 南京大学循环每一期每一篇文章概括
     *
     * @throws IOException
     */
    public void getChildAddress() throws IOException {
        for (int i = 74; i < 88; i++) {
            String url = "https://jns.nju.edu.cn/CN/volumn/volumn_" + i + ".shtml";
            String htmlDocument = ReptileUtil.getHtmlDocument(url);
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
        String htmlDocument = ReptileUtil.getHtmlDocument(url);
        Document doc = Jsoup.parse(htmlDocument);
        if (doc.selectFirst("span.change-section") != null && doc.selectFirst("span.change-section").selectFirst("a") != null) {
            Element label = Objects.requireNonNull(doc.selectFirst("span.change-section")).selectFirst("a");
            String onclick = null;
            if (label != null) {
                onclick = label.attr("onclick");
            }
            String urlAttr = null;
            String year = null;
            if (onclick != null) {
                urlAttr = ReptileUtil.extractStringBetweenLastTwoSingleQuotes(onclick);
            }
            String contentUrl = "https://jns.nju.edu.cn/" + urlAttr;
            if (urlAttr.length() > 3) {
                getLiteratureContent(contentUrl);
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
        String authorsCn = null;
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
        }


        //英文
        Element en = doc.selectFirst("div.title-en");
        String titleEn = "";
        String authorsEn = null;
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
        }


        // 摘要cn
        Element abstractDivCn = doc.selectFirst("div.zhaiyao-cn-content");
        String keywordCn = null;
        String abstractCn = null;
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
        String abstractEn = null;
        String keywordEn = null;
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
                    String path = "\\pic\\南京大学学报\\" + pa;
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
        metadataHtml.setType(2);
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




