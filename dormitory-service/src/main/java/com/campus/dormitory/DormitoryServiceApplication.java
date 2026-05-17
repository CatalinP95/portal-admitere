package com.campus.dormitory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.campus.dormitory.client")
@EnableScheduling
@EnableAsync
@EnableJpaRepositories(basePackages = "com.campus.dormitory.repository.jpa")
public class DormitoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DormitoryServiceApplication.class, args);
    }
}
