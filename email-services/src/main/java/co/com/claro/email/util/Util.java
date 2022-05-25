package co.com.claro.email.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {

	private Util() {
		throw new RuntimeException("Utility class");
	}

	public static String dateToString(Date date, String format) {

		final var dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);

	}

	public static String objectToJson(Object object) throws JsonProcessingException {

		final var mapper = new ObjectMapper();
		return mapper.writeValueAsString(object);

	}

}
