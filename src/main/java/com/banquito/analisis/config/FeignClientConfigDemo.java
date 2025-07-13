package com.banquito.analisis.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!demo") // Solo habilita FeignClient si NO es demo
@EnableFeignClients(basePackages = "com.banquito.analisis.client")
public class FeignClientConfigDemo {
} 