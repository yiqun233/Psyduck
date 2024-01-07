package com.yiqun233.psyduck.reptile.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class SimpleWebScraper {
    public static void main(String[] args) {
        // 设置WebDriver的路径
        System.setProperty("webdriver.edge.driver", "D:\\workSpace\\driver\\msedgedriver.exe");

        // 创建Chrome浏览器实例
        WebDriver driver = new EdgeDriver();

        // 打开目标网页
        driver.get("http://www.cpedm.com/CN/10.11698/PED.20230152#9");

        // 执行JavaScript代码，以模拟浏览器行为（如果需要）
        // driver.executeScript("javascript:...");

        // 获取HTML内容
        String htmlContent = driver.getPageSource();

        // 打印HTML内容
        System.out.println(htmlContent);

        // 关闭浏览器
        driver.quit();
    }
}
