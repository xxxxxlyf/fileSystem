package com.file_system;

import com.file_system.config.MvcCors;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication()
@MapperScan(basePackages ="com.file_system.dao")
public class FileSystemApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(FileSystemApplication.class, args);
    }


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FileSystemApplication.class);
    }
}
