package ticktrack.frontend;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import ticktrack.frontend.security.WebSecurityConfig;

@SpringBootApplication
public class Frontend {

   @Autowired
   Logger logger;

   @Bean
   @Scope("singleton")
   public Logger logger() {
      return LoggerFactory.getLogger("");
   }

   @Bean
   @Scope("singleton")
   public OkHttpClient httpClient() {
      logger.info("initializing http client");
      return new OkHttpClient();
   }

   public static void main(String[] args) {
      SpringApplication.run(Frontend.class, args);
   }

}