package co.com.claro.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableRetry
@EnableAsync
@SpringBootApplication
public class SmsServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmsServicesApplication.class, args);
	}

}
