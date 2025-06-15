package cv.claims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "cv.claims")
public class ClaimsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClaimsApplication.class, args);
    }

}
