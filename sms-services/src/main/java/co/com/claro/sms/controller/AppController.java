package co.com.claro.sms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.claro.sms.dto.RequestDTO;
import co.com.claro.sms.dto.RequestDecriptDTO;
import co.com.claro.sms.dto.RequestEncriptTokenDTO;
import co.com.claro.sms.entity.Log;
import co.com.claro.sms.service.LogService;
import co.com.claro.sms.services.SMSServices;
import co.com.claro.sms.util.AESUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class AppController {

	@Autowired
	private SMSServices services;

	@Autowired
	private LogService logService;

	@PostMapping("/sms/send")
	public void send(@RequestBody RequestDTO request) {

		log.info("[[START]] request: {}", request);

		if (request == null || request.getPhone() == null) {
			throw new RuntimeException("Telefono invalido");
		}

		if (request == null || request.getMessage() == null || request.getMessage().isBlank()
				|| request.getMessage().length() > 150) {
			throw new RuntimeException("Mensaje invalido");
		}

		services.sendSMS(request);

	}

	@PostMapping("/sms/encript")
	public String encript(@RequestBody RequestEncriptTokenDTO encriptTokenDTO) throws Exception {

		log.info("[[START]] encriptTokenDTO: {}", encriptTokenDTO);

		String data = new ObjectMapper().writeValueAsString(encriptTokenDTO.getData());
		log.info("data: {}", data);

		return AESUtil.encrypt(data, encriptTokenDTO.getKey());

	}

	@PostMapping(path = "/sms/decript", produces = MediaType.APPLICATION_JSON_VALUE)
	public String decript(@RequestBody RequestDecriptDTO requestDecript) {

		return services.decript(requestDecript.getToken());

	}

	@GetMapping("/log")
	public List<Log> findByDate() {
		return logService.findByDate();
	}

}
