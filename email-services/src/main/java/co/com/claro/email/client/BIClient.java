package co.com.claro.email.client;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import co.com.claro.email.dto.email.notification.EMAILResponse;
import co.com.claro.email.exception.CallLegacyException;
import co.com.claro.email.util.Util;

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

	@Value("${clientBi.url:http://100.126.21.189:7777/BIZInteractions/Rest/V1.0/BizInteractionsApi/put/}")
	private String url;

	@Autowired
	private RestTemplate restTemplate;

	private static final Logger log = LoggerFactory.getLogger(BIClient.class);

	@Async
	@Retryable(value = { Exception.class, RuntimeException.class }, maxAttempts = 2, backoff = @Backoff(1000))
	public void createHeader(String message) {

		final var watch = new StopWatch();
		watch.start();

		log.info("[[Start]] createHeader");

		try {

			var finalurl = url + operation + "/" + message;

			log.trace("path url: {}", finalurl);

			final var ureBuilder = UriComponentsBuilder.fromHttpUrl(finalurl);

			ureBuilder.queryParam("system", system);

			ureBuilder.queryParam("user", user);

			ureBuilder.queryParam("password", password);

			// 2020-02-25T16:39:28.781
			final var requestDate = Util.dateToString(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSS");
			ureBuilder.queryParam("requestDate", requestDate);

			log.debug("encode url: {}", ureBuilder.build().encode().toUri());

			final ResponseEntity<EMAILResponse> responseEMAIL = restTemplate
					.exchange(ureBuilder.build().encode().toUri(), HttpMethod.PUT, null, EMAILResponse.class);
			final var response = responseEMAIL.getBody();
			log.debug("responseBI: {}", response);

			if (response == null || !Boolean.valueOf(response.getIsValid()))
				throw new CallLegacyException("No se logro crear la BI");

		} finally {
			watch.stop();
			log.info("[[end]] createHeader, el servicio tardo {} milisegundos", watch.getTotalTimeMillis());
		}

	}

	@Recover
	public void retrycreateHeader(Exception e, String message) {

		log.error("createHeader Exception: {}", e.getMessage(), e);

	}

}
