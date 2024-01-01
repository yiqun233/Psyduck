package com.yiqun233.psyduck.reptile.controller;

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

    @GetMapping("/test")
    public String getApiList() throws IOException {
        tMetadataHtmlService.getChildAddress();
        return "success";
    }
}
