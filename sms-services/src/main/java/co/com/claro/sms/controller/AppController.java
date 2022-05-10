package co.com.claro.sms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.com.claro.sms.dto.RequestDTO;
import co.com.claro.sms.entity.Log;
import co.com.claro.sms.service.LogService;
import co.com.claro.sms.services.SMSServices;
import co.com.claro.sms.util.AESUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1")
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
		
		if (request == null || request.getMessage() == null || request.getMessage().isBlank() || request.getMessage().length() > 150) {
			throw new RuntimeException("Mensaje invalido");
		}
		
		services.sendSMS(request);

	}

	@GetMapping("/sms/encript")
	public String encript(@RequestHeader("data") String data, @RequestHeader("key") String key) throws Exception {

		return AESUtil.encrypt(data, key);

	}

	@GetMapping("/sms/decript")
	public Map<String, String> decript(@RequestHeader("token") String token) {

		return services.decript(token);
		
	}
	
	@GetMapping("/log")
	public List<Log> findByDate(){
		return logService.findByDate();
	}

}
