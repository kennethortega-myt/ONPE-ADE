package pe.gob.onpe.scebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableAutoConfiguration
//@ComponentScan(basePackages = {"pe.gob.pe.onpe.scebackend,pe.gob.pe.onpe.scebackend.rest.controller"})
public class SceBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(SceBackApplication.class, args);
    }

}
