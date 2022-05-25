package co.com.claro.email.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import co.com.claro.email.dto.RequestDTO;
import co.com.claro.email.dto.ResponseDTO;
import co.com.claro.email.service.EmailService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1")
public class AppController {

	@Autowired
	private EmailService services;

	@CrossOrigin
	@PostMapping("/email/send")
	public ResponseDTO send(@RequestBody RequestDTO request) throws JsonProcessingException {

		log.info("[[START]] request: {}", request);

		final var watch = new StopWatch();
		watch.start();

		try {

			if (request == null || request.getEmail() == null)
				throw new RuntimeException("Email invalido");

			return services.sendEMAIL(request);

		} finally {
			watch.stop();
			log.info("[[END]] el servicio tardo: {}", watch.getTotalTimeMillis());
		}

	}

}
