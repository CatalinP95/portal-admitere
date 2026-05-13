package com.campus.dormitory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.campus.dormitory.repository.jpa")
public class DormitoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DormitoryServiceApplication.class, args);
    }
}
