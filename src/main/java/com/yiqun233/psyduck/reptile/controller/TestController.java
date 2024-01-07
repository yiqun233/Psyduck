package com.yiqun233.psyduck.reptile.controller;

import com.yiqun233.psyduck.reptile.domain.TMetadataHtml;
import com.yiqun233.psyduck.reptile.service.TMetadataHtmlChaptersService;
import com.yiqun233.psyduck.reptile.service.TMetadataHtmlResService;
import com.yiqun233.psyduck.reptile.service.TMetadataHtmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * /**
 *
 * @author yiqun
 * @date 2023/12/29 13:38
 * @description
 */
@RestController
@RequestMapping("/api/")
public class TestController {

    @Autowired
    TMetadataHtmlService tMetadataHtmlService;

    @Autowired
    TMetadataHtmlResService tMetadataHtmlResService;

    @Autowired
    TMetadataHtmlChaptersService tMetadataHtmlChaptersService;


    @GetMapping("/test")
    public String getApiList() throws IOException {
        tMetadataHtmlService.getChildAddress();
        return "success";
    }
    @GetMapping("/test2")
    public String getApiList2() throws IOException {
        tMetadataHtmlResService.getChildAddress();
        return "success";
    }

    @GetMapping("/test3")
    public String getApiList3() throws IOException {
        tMetadataHtmlChaptersService.getChildAddress();
        return "success";
    }

    @GetMapping("/test4")
    public String getApiList4() throws IOException {
        tMetadataHtmlChaptersService.getChildAddress2();
        return "success";
    }



}
