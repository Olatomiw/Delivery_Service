package com.deliverywindow.deliveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@EnableAsync
public class DeliveryServiceApplication {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SpringApplication.run(DeliveryServiceApplication.class, args);
    }
}