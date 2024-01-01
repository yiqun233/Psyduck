package com.yiqun233.psyduck.reptile.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName t_metadata_html_res
 */
@Data
@TableName("t_metadata_html_res")
public class TMetadataHtmlRes implements Serializable {
    /**
     *
     */
    @TableId("id")
    private String id;

    /**
     * 文章id
     */
    @TableField("metadata_html_id")
    private String metadataHtmlId;

    /**
     * 1图片  2表格
     */
    @TableField("type")
    private Integer type;

    /**
     * 路径，格式： /文章文件夹/图片.jpg
     */
    @TableField("path")
    private String path;

    /**
     * 表格源html
     */
    @TableField("table_html")
    private String tableHtml;

    private static final long serialVersionUID = 1L;


}
