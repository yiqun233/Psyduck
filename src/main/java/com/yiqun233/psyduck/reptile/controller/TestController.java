package com.yiqun233.psyduck.reptile.controller;

import com.yiqun233.psyduck.reptile.service.ReptileService1;
import com.yiqun233.psyduck.reptile.service.ReptileService2;
import com.yiqun233.psyduck.reptile.service.ReptileService3;
import com.yiqun233.psyduck.reptile.service.ReptileService4;
import com.yiqun233.psyduck.reptile.service.ReptileService5;
import com.yiqun233.psyduck.reptile.service.ReptileService6;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
    ReptileService1 reptileService1;

    @Autowired
    ReptileService2 reptileService2;

    @Autowired
    ReptileService3 reptileService3;

    @Autowired
    ReptileService4 reptileService4;

    @Autowired
    ReptileService5 reptileService5;
    @Autowired
    ReptileService6 reptileService6;


    @GetMapping("/test")
    public String getApiList() throws IOException {
        reptileService2.getChildAddress();
        return "success";
    }

    @GetMapping("/test2")
    public String getApiList2() throws IOException {
        reptileService5.getChildAddress();
        return "success";
    }

    @GetMapping("/test3")
    public String getApiList3() throws IOException {
        reptileService4.getChildAddress();
        return "success";
    }

    @GetMapping("/test4")
    public String getApiList4() throws IOException {
        reptileService4.getChildAddress2();
        return "success";
    }


}
