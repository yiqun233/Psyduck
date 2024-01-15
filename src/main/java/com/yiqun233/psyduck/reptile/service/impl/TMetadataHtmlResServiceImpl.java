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
import com.yiqun233.psyduck.reptile.service.TMetadataHtmlResService;
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
 * @author YI
 * @description 针对表【t_metadata_html_res】的数据库操作Service实现
 * @createDate 2023-12-29 13:07:02
 */
@Slf4j
@Service
public class TMetadataHtmlResServiceImpl extends ServiceImpl<TMetadataHtmlResMapper, TMetadataHtmlRes>
        implements TMetadataHtmlResService {


}




