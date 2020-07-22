//package com.demodashi;
//
//import com.demodashi.annotation.GrpcService;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.ConfigurableApplicationContext;
//
//import java.util.Map;
//
///**
// * Spring Boot 启动器
// */
//@SpringBootApplication
//public class Launcher {
//
//    public static void main(String[] args) {
//        // 启动SpringBoot web
//        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(Launcher.class, args);
//        Map<String, Object> grpcServiceBeanMap =  configurableApplicationContext.getBeansWithAnnotation(GrpcService.class);
//        GrpcLauncher grpcLauncher = configurableApplicationContext.getBean("grpcLauncher", GrpcLauncher.class);
//        grpcLauncher.grpcStart(grpcServiceBeanMap);
//    }
//}
