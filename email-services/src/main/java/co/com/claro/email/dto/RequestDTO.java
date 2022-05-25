package co.com.claro.email.dto;

import java.io.Serializable;
import java.util.Map;

import co.com.claro.email.dto.enums.TypeDocument;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestDTO implements Serializable {

	private String email;
	private String message;
	private String subject;

	private TypeDocument clientTypeDocument;
	private String clientDocument;
	private String clientAccount;
	private String clientEmail;
	private String clientName;

	private TypeDocument asesorTypeDocument;
	private String asesorDocument;
	private String asesorCod;
	private String asesorName;
	private String asesorSignum;

	private Map<String, Object> bi;

}