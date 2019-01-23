package erik.demo.bootstrap;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan({"erik.demo.controller"})
public class ApplicationBootStrap {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationBootStrap.class, args);
    }


}
