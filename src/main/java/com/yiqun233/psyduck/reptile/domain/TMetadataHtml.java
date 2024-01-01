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
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 作者
     */
    @TableField("authors")
    private String authors;

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
     * 正文
     */
    @TableField("main_body")
    private String mainBody;

    /**
     * 未加工的源html内容
     */
    @TableField("metadata_str")
    private String metadataStr;

}
