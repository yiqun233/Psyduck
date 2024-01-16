package com.yiqun233.psyduck.reptile.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yiqun233.psyduck.reptile.domain.TMetadataHtml;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlChaptersMapper;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlMapper;
import com.yiqun233.psyduck.reptile.mapper.TMetadataHtmlResMapper;
import com.yiqun233.psyduck.reptile.service.ReptileService3;
import com.yiqun233.psyduck.reptile.util.ReptileUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

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
public class ReptileServiceImpl3 implements ReptileService3 {


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
        //2020 年开始
        for (int i = 2020; i < 2024; i++) {
            for (int j = 1; j < 5; j++) {
                String url = "https://hysy.shopc.com.cn/cn/article/" + i + "/" + j;
                String htmlDocument = ReptileUtil.getHtmlDocument(url);
                Document doc = Jsoup.parse(htmlDocument);

                Elements select = doc.select("div.article-list");
                for (Element element : select) {
                    Elements a = element.select("font.font2.count1").select("a");
                    String contentUrl = a.attr("href");
                    String title = element.selectFirst("div.article-list-title.clearfix").text();
                    try {
                        getLiteratureContent("https://hysy.shopc.com.cn" + contentUrl, title);
                    } catch (Exception e) {
                        log.error("失败的地址" + contentUrl);
                    }
                }
            }
        }
    }


    public void getLiteratureContent(String url, String title) throws IOException {
        String id = IdUtil.simpleUUID();
        String content = ReptileUtil.getHtmlDocument(url);
        Document doc = Jsoup.parse(content);
        Elements element = doc.select("ul.marginT.addresswrap");
        String orgCn = "";
        String orgEn = "";
        if (element.size() > 1) {
            orgCn = element.get(0).text();
            orgEn = element.get(1).text();
        } else if (element.size() == 1) {
            orgCn = element.get(0).text();
            orgEn = element.get(0).text();
        }
        QueryWrapper<TMetadataHtml> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TMetadataHtml::getTitleCn, title);
        TMetadataHtml tMetadataHtml = tMetadataHtmlMapper.selectOne(queryWrapper);
        if (tMetadataHtml != null) {
            tMetadataHtml.setSource(url);
            tMetadataHtml.setOrganizationCn(orgCn);
            tMetadataHtml.setOrganizationEn(orgEn);
            tMetadataHtmlMapper.updateById(tMetadataHtml);
        }
    }

}
