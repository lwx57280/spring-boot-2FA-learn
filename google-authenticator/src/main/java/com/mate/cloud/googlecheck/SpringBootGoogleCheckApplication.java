package com.mate.cloud.googlecheck;

import com.mate.cloud.doc.annotation.EnableSpringDoc;
import com.mate.cloud.googlecheck.config.GoogleAuthenticatorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;



@EnableSpringDoc(value = "springdoc", isMicro = false)
@SpringBootApplication
@EnableConfigurationProperties({GoogleAuthenticatorProperties.class})
public class SpringBootGoogleCheckApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootGoogleCheckApplication.class, args);
    }

}