package co.com.claro.sms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.claro.sms.dto.RequestDTO;
import co.com.claro.sms.dto.RequestDecriptDTO;
import co.com.claro.sms.dto.RequestEncriptTokenDTO;
import co.com.claro.sms.entity.Log;
import co.com.claro.sms.exception.BadRequestException;
import co.com.claro.sms.exception.BussinesException;
import co.com.claro.sms.service.LogService;
import co.com.claro.sms.service.SMSServices;
import co.com.claro.sms.util.AESUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1")
public class AppController {

	@Autowired
	private SMSServices services;

	@Autowired
	private LogService logService;

	@CrossOrigin
	@PostMapping("/sms/send")
	public void send(@RequestBody RequestDTO request) throws JsonProcessingException {

		log.info("[[START]] request: {}", request);

		final var watch = new StopWatch();
		watch.start();

		try {

			if (request == null || request.getPhone() == null)
				throw new BadRequestException("Telefono invalido");

			if (request.getMessage() == null || request.getMessage().isBlank() || request.getMessage().length() > 150)
				throw new BadRequestException("Mensaje invalido");

			services.sendSMS(request);

		} finally {
			watch.stop();
			log.info("[[END]] el servicio tardo: {}", watch.getTotalTimeMillis());
		}

	}

	@CrossOrigin
	@PostMapping("/sms/encript")
	public String encript(@RequestBody RequestEncriptTokenDTO encriptTokenDTO) {

		log.info("[[START]] encriptTokenDTO: {}", encriptTokenDTO);

		String data;

		try {

			data = new ObjectMapper().writeValueAsString(encriptTokenDTO.getData());

			log.info("data: {}", data);

			final var encrypt = AESUtil.encrypt(data, encriptTokenDTO.getKey());
			log.info("encrypt: {}", encrypt);
			return encrypt;

		} catch (JsonProcessingException e) {
			throw new BussinesException(e.getMessage(), e);
		}

	}

	@CrossOrigin
	@PostMapping(path = "/sms/decript", produces = MediaType.APPLICATION_JSON_VALUE)
	public String decript(@RequestBody RequestDecriptDTO requestDecript) {

		return services.decript(requestDecript.getToken());

	}

	@CrossOrigin
	@GetMapping("/log")
	public List<Log> findByDate() {
		return logService.findByDate();
	}

}
