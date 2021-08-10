package com.h3c.solutionhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

//@EnableScheduling
@SpringBootApplication
@EnableAsync
public class SolutionhubApplication {

    public static void main(String[] args) {
        SpringApplication.run(SolutionhubApplication.class, args);
    }
}

//@SpringBootApplication
//public class SolutionhubApplication extends SpringBootServletInitializer {
//
//    public static void main(String[] args) {
//        SpringApplication.run(SolutionhubApplication.class, args);
//    }
//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(SolutionhubApplication.class);
//    }
//
//}
