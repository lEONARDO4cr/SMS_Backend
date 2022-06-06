package co.com.claro.sms.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import brave.Tracer;
import co.com.claro.sms.client.BIClient;
import co.com.claro.sms.dto.RequestDTO;
import co.com.claro.sms.dto.ResponseDTO;
import co.com.claro.sms.dto.sms.notification.HeaderRequest;
import co.com.claro.sms.dto.sms.notification.MessageRequest;
import co.com.claro.sms.dto.sms.notification.RequestSMS;
import co.com.claro.sms.dto.sms.notification.SMSResponse;
import co.com.claro.sms.entity.Log;
import co.com.claro.sms.exception.BussinesException;
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

	@Value("${sms.token.key:SMSEMAIL}")
	private String tokenKey;

	@Value("${sms.url:http://100.126.21.189:7777/Notification/V2.0/Rest/PutMessage}")
	private String url;

	@Autowired
	private Tracer tracer;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private LogService logService;

	@Autowired
	private BIClient biClient;

	public ResponseDTO sendSMS(RequestDTO request) throws JsonProcessingException {

		log.info("[[START]] decriptToken: {}", request);

		sendSMS(request.getPhone(), request.getMessage());
		insertLog(request);

		if (request.getBi() != null) {

			final var message = new ObjectMapper().writeValueAsString(request.getBi());
			biClient.createHeader(message);

		}

		return new ResponseDTO();
	}

	private void sendSMS(String phone, String message) {

		final var requestSMS = buildRequest(phone, message);
		log.info("requestSMS: {}", requestSMS);

		final var httpEntity = new HttpEntity<>(requestSMS);
		final ResponseEntity<SMSResponse> responseSMS = restTemplate.exchange(url, HttpMethod.PUT, httpEntity,
				SMSResponse.class);
		log.info("responseSMS: {}", responseSMS.getBody());

	}

	public void insertLog(RequestDTO request) {

		final var auditLog = new Log();
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

		logService.insert(auditLog);

	}

	public String decript(String token) {

		final var key = generateKey();
		return tokenToMap(token, key);

	}

	public String generateKey() {

		final var date = Util.dateToString(new Date(), "yyyyMMdd");
		return tokenKey + date;

	}

	public RequestSMS buildRequest(String phone, String message) {

		final var requestSMS = new RequestSMS();

		final var headerRequest = new HeaderRequest();
		headerRequest.setIpApplication("127.0.0.1");
		headerRequest.setPassword("system432");
		headerRequest.setRequestDate(Util.dateToString(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSS"));
		headerRequest.setSystem("system432");
		headerRequest.setTarget("target");
		headerRequest.setTraceabilityId("traceabilityId436");
		headerRequest.setTransacitonID(getTraceId());
		headerRequest.setUser("user433");

		requestSMS.setHeaderRequest(headerRequest);

		final var messageRequest = new MessageRequest();
		messageRequest.setCommunicationOrigin("TCRM");
		messageRequest.addCommunicationType("COMERCIAL");
		messageRequest.setContentType("MESSAGE");
		messageRequest.setDeliveryReceipts("YES");
		messageRequest.setMessageContent(message);
		messageRequest.addProfileId("SMS_FS_TCRM1");
		messageRequest.setPushType("SINGLE");
		messageRequest.setTypeCostumer("25987563");

		messageRequest.addMessageBox("SMS", phone);

		try {

			// Convertir a string
			final var jsonMessage = Util.objectToJson(messageRequest);
			requestSMS.setMessage(jsonMessage);

		} catch (final JsonProcessingException e) {
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

	private String tokenToMap(String token, String key) {

		try {

			final var desencriptado = AESUtil.decrypt(token, key);
			log.info("Token decriptado: {}", desencriptado);

			return desencriptado;

		} catch (final Exception e) {
			throw new BussinesException(e.getMessage());
		}

	}

}
