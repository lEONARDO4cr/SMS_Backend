package co.com.claro.sms.client;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;

import co.com.claro.sms.dto.sms.notification.SMSResponse;
import co.com.claro.sms.util.Util;

@Service
public class BIClient {

	@Value("${clientBi.getoperation.default.value:setPresencialBizInteraction}")
	public String operation;

	@Value("${clientBi.system.default.value:Vista360}")
	private String system;

	@Value("${clientBi.user.default.value:user1}")
	private String user;

	@Value("${clientBi.password.default.value:password1}")
	private String password;

	@Autowired
	private RestTemplate restTemplate;

	private static final Logger log = LoggerFactory.getLogger(BIClient.class);

	public void createHeader(String message) throws JsonProcessingException {

		String url = "http://100.126.21.189:7777/BIZInteractions/Rest/V1.0/BizInteractionsApi/put/";

		url = url + "setPresencialBizInteraction/";

		log.info("url: {}", url);

		url += message;

		log.info("url: {}", url);

		UriComponentsBuilder ureBuilder = UriComponentsBuilder.fromHttpUrl(url);

		ureBuilder.queryParam("system", system);

		ureBuilder.queryParam("user", user);

		ureBuilder.queryParam("password", password);

		// 2020-02-25T16:39:28.781
		String requestDate = Util.dateToString(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSS");
		ureBuilder.queryParam("requestDate", requestDate);

		log.info("url: {}", ureBuilder.build().encode().toUri());

		ResponseEntity<SMSResponse> responseSMS = restTemplate.exchange(ureBuilder.build().encode().toUri(),
				HttpMethod.PUT, null, SMSResponse.class);
		log.info("responseBI: {}", responseSMS.getBody());

	}

}
