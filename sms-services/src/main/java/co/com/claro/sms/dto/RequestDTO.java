package co.com.claro.sms.dto;

import java.io.Serializable;

import co.com.claro.sms.dto.enums.TypeDocument;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestDTO implements Serializable {

	private String phone;
	private String message;
	
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

}
