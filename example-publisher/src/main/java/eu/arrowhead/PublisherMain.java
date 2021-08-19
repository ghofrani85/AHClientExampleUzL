package eu.arrowhead;

import eu.arrowhead.common.CommonConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { CommonConstants.BASE_PACKAGE }) // TODO: add custom packages if any
public class PublisherMain {
	public static void main(final String[] args) {
		SpringApplication.run(PublisherMain.class, args);
	}
}
