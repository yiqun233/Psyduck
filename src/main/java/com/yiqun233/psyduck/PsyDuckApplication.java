package com.yiqun233.psyduck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author Qun Q Yi
 */
@SpringBootApplication
@EnableCaching
public class PsyDuckApplication {

    public static void main(String[] args) {
        SpringApplication.run(PsyDuckApplication.class, args);
    }

}
