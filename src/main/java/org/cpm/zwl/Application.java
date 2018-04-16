package org.cpm.zwl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * 
 * @author CPM
 *
 */
@SpringBootApplication
@ServletComponentScan
public class Application {

  public static void main(String[] args) throws Exception {
    SpringApplication application = new SpringApplication(Application.class);
    application.run(args);
  }
}
