package com.rmb.cecomments;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CeCommentsApplication {

   /**
    * The entry point of application.
    *
    * @param args the input arguments
    */
   public static void main(String[] args) {
      log.info("STARTING THE APPLICATION");
      SpringApplication.run(CeCommentsApplication.class, args);
      log.info("APPLICATION FINISHED");
   }

}
