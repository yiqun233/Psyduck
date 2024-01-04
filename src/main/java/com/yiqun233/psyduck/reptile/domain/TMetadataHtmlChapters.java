package com.yiqun233.psyduck.reptile.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName t_metadata_html_chapters
 */
@Data
@TableName(value ="t_metadata_html_chapters")
public class TMetadataHtmlChapters implements Serializable {
    /**
     * 章节id
     */
    @TableId
    private String id;

    /**
     * 文章id
     */
    @TableField("metadata_html_id")
    private String metadataHtmlId;

    /**
     * 章节标题1
     */
    @TableField("chapter_title1")
    private String chapterTitle1;

    /**
     * 章节标题2
     */
    @TableField("chapter_title2")
    private String chapterTitle2;

    /**
     * 章节标题3
     */
    @TableField("chapter_title3")
    private String chapterTitle3;

    /**
     * 章节正文内容
     */
    @TableField("content")
    private String content;

    /**
     * 顺序
     */
    @TableField("sequence")
    private Integer sequence;

    @TableField(exist = false)
    private String attr;

}
