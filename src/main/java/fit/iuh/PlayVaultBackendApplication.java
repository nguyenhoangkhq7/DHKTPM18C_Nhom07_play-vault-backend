package fit.iuh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ConfigurationPropertiesScan("fit.iuh.config") // ← thêm dòng này, trỏ đúng package config
public class PlayVaultBackendApplication {

   public static void main(String[] args) {
      SpringApplication.run(PlayVaultBackendApplication.class, args);
   }

}
