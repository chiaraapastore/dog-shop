package dogshop.market;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients(basePackages = "dogshop.market.client")
public class DogShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(DogShopApplication.class, args);
    }
}
