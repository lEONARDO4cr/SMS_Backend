package co.com.claro.sms.client;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import co.com.claro.sms.dto.bi.HeaderRequestBiDTO;
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

	public void createHeader(String message, Map<String, Object> bi) {

		HeaderRequestBiDTO request = new HeaderRequestBiDTO();
		request.setGetOperation(operation);

		request.setSystem(system);

		request.setUser(user);

		request.setPassword(password);

		// 2020-02-25T16:39:28.781
		String requestDate = Util.dateToString(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSS");
		request.setRequestDate(requestDate);

		request.setMessage(message);

		String url = "http://100.126.21.189:7777/BIZInteractions/Rest/V1.0/BizInteractionsApi/put/setPresencialBizInteraction/";

		UriComponentsBuilder ureBuilder = UriComponentsBuilder.fromHttpUrl(url);

		ureBuilder.queryParam("system", system);

		ureBuilder.queryParam("user", user);

		ureBuilder.queryParam("password", password);

		ureBuilder.queryParam("requestDate", requestDate);

		log.info("url: {}", ureBuilder.toUriString());

//		HttpEntity<HeaderRequestBiDTO> httpEntity = new HttpEntity<>(request);
//		ResponseEntity<SMSResponse> responseSMS = restTemplate.exchange(
//				"http://100.126.21.189:7777/BIZInteractions/Rest/V1.0/BizInteractionsApi/put?system=Vista360&user=user1&password=passwor",
//				HttpMethod.PUT, httpEntity, SMSResponse.class);
//		log.info("responseBI: {}", responseSMS.getBody());

	}

}
