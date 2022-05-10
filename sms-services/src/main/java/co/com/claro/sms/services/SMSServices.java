package co.com.claro.sms.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

import brave.Tracer;
import co.com.claro.sms.dto.RequestDTO;
import co.com.claro.sms.dto.ResponseDTO;
import co.com.claro.sms.dto.sms.notification.HeaderRequest;
import co.com.claro.sms.dto.sms.notification.MessageRequest;
import co.com.claro.sms.dto.sms.notification.RequestSMS;
import co.com.claro.sms.dto.sms.notification.SMSResponse;
import co.com.claro.sms.entity.Log;
import co.com.claro.sms.service.LogService;
import co.com.claro.sms.util.AESUtil;
import co.com.claro.sms.util.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SMSServices {

	public static final String INVALID_TOKEN = "El token enviado en la URL es invalido valor de token: %s ";
	public static final String PARAMETER_TELEFONOS = "telefonos";
	public static final String MISSING_VALUE_ERROR = "Hace falta el parametro '%s' en la url de inicio";
	public static final String MISSING_VALUE_ERROR2 = "Hace falta el valor para el parametro '%s' en la url de inicio";

	@Value("${token.key:MICLAROA}")
	private String tokenKey;

	@Autowired
	private Tracer tracer;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private LogService logService;

	public ResponseDTO sendSMS(RequestDTO request) {

		log.info("[[START]] decriptToken: {}", request);

		sendSMS(request.getPhone(), request.getMessage());
		insertLog(request);

		return new ResponseDTO();
	}

	private void sendSMS(String phone, String message) {

		RequestSMS requestSMS = buildRequest(phone, message);
		log.info("requestSMS: {}", requestSMS);

		HttpEntity<RequestSMS> httpEntity = new HttpEntity<>(requestSMS);
		ResponseEntity<SMSResponse> responseSMS = restTemplate.exchange(
				"http://100.126.21.189:7777/Notification/V2.0/Rest/PutMessage", HttpMethod.PUT, httpEntity,
				SMSResponse.class);
		log.info("responseSMS: {}", responseSMS.getBody());

	}

	public void insertLog(RequestDTO request) {

		Log auditLog = new Log();
		auditLog.setAccount(request.getClientAccount());
		auditLog.setAsesorCod(request.getAsesorCod());
		auditLog.setAsesorDocument(request.getAsesorDocument());
		auditLog.setAsesorName(request.getAsesorName());
		auditLog.setChannel("SMS");
		auditLog.setDocument(request.getClientDocument());
		auditLog.setEmail(request.getClientEmail());
		auditLog.setMessage(request.getMessage());
		auditLog.setPhone(request.getPhone());
		auditLog.setMessage(request.getMessage());
		auditLog.setTypeDocument(request.getClientTypeDocument());

		auditLog = logService.insert(auditLog);
		log.info("auditLog: {}", auditLog);

	}

	public Map<String, String> decript(String token) {

		String key = generateKey();
		return tokenToMap(token, key);

	}

	public String generateKey() {

		String date = Util.dateToString(new Date(), "yyyyMMdd");
		return tokenKey + date;

	}

	public RequestSMS buildRequest(String phone, String message) {

		RequestSMS requestSMS = new RequestSMS();

		HeaderRequest headerRequest = new HeaderRequest();
		headerRequest.setIpApplication("127.0.0.1");
		headerRequest.setPassword("system432");
		headerRequest.setRequestDate(Util.dateToString(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSS"));
		headerRequest.setSystem("system432");
		headerRequest.setTarget("target");
		headerRequest.setTraceabilityId("traceabilityId436");
		headerRequest.setTransacitonID(getTraceId());
		headerRequest.setUser("user433");

		requestSMS.setHeaderRequest(headerRequest);

		MessageRequest messageRequest = new MessageRequest();
		messageRequest.setCommunicationOrigin("TCRM");
		messageRequest.addCommunicationType("COMERCIAL");
		messageRequest.setContentType("MESSAGE");
		messageRequest.setDeliveryReceipts("YES");
		messageRequest.setMessageContent(message);
		messageRequest.addProfileId("SMS_FS_TCRM1");
		messageRequest.setPushType("SINGLE");
		messageRequest.setTypeCostumer("SINGLE");

		messageRequest.addMessageBox("SMS", phone);

		try {

			// Convertir a string
			String jsonMessage = Util.objectToJson(messageRequest);
			requestSMS.setMessage(jsonMessage);

		} catch (JsonProcessingException e) {
			log.error("JsonProcessingException: {}", e.getMessage(), e);
		}

		return requestSMS;

	}

	/**
	 * Metodo para obtener el id de la transaccion
	 *
	 * @return trace id
	 */
	public String getTraceId() {

		if (tracer != null && tracer.currentSpan() != null && tracer.currentSpan().context() != null)
			return tracer.currentSpan().context().traceIdString();

		return null;
	}

	private Map<String, String> tokenToMap(String token, String key) {

		Map<String, String> mapParameters = new HashMap<>();

		try {

			final String desencriptado = AESUtil.decrypt(token, key);

			if (desencriptado == null || desencriptado.isEmpty()) {
				throw new RuntimeException(INVALID_TOKEN);
			}

			final String[] arrOfStr = desencriptado.split("&");

			for (int i = 0; i < arrOfStr.length; i++) {
				if (arrOfStr[i].split("=").length == 1) {
					throw new RuntimeException(
							"Error:" + String.format(MISSING_VALUE_ERROR2, arrOfStr[i].split("=")[0]));
				}
				mapParameters.put(arrOfStr[i].split("=")[0], arrOfStr[i].split("=")[1]);
			}

		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return mapParameters;
	}

}
