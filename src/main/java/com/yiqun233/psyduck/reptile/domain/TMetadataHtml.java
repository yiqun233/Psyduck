package com.yiqun233.psyduck.reptile.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName t_metadata_html
 */
@Data
@TableName("t_metadata_html")
public class TMetadataHtml implements Serializable {
    /**
     *
     */
    @TableId("id")
    private String id;

    /**
     * 1沉积学报 2南京大学报 3海洋石油 4石油勘探与开发 5石油与天然气地质 6西南石油大学学报
     */
    @TableField("type")
    private Integer type;

    /**
     * 标题-中文
     */
    @TableField("title_cn")
    private String titleCn;

    /**
     * 标题-英文
     */
    @TableField("title_en")
    private String titleEn;

    /**
     * 作者-中文
     */
    @TableField("authors_cn")
    private String authorsCn;

    /**
     * 作者-英文
     */
    @TableField("authors_en")
    private String authorsEn;

    /**
     * doi
     */
    @TableField("doi")
    private String doi;

    /**
     * 摘要-中文
     */
    @TableField("digest_cn")
    private String digestCn;

    /**
     * 摘要-英文
     */
    @TableField("digest_en")
    private String digestEn;

    /**
     * 关键词-中文
     */
    @TableField("keywords_cn")
    private String keywordsCn;

    /**
     * 关键词-英文
     */
    @TableField("keywords_en")
    private String keywordsEn;

    /**
     * 正文 （待删除）
     */
    @TableField("main_body")
    private String mainBody;

    /**
     * 未加工的源html内容
     */
    @TableField("metadata_str")
    private String metadataStr;

    /**
     * 文章发布组织-中文
     */
    @TableField("organization_cn")
    private String organizationCn;

    /**
     * 文章发布组织-英文
     */
    @TableField("metadata_str")
    private String organizationEn;

    /**
     * url地址
     */
    @TableField("source")
    private String source;

}
