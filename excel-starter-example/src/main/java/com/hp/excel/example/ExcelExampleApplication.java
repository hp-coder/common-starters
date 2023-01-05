package com.hp.excel.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author HP 2023/1/5
 */
@ComponentScan(basePackages = "com.hp.excel")
@SpringBootApplication
public class ExcelExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExcelExampleApplication.class, args);
    }
}
