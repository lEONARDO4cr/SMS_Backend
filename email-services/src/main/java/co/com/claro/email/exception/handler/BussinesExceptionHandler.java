package co.com.claro.email.exception.handler;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import brave.Tracer;
import co.com.claro.email.dto.ResponseDTO;
import co.com.claro.email.exception.BussinesException;

@ControllerAdvice
public class BussinesExceptionHandler {

	@Autowired
	private Tracer tracer;

	@ExceptionHandler
	public ResponseEntity<ResponseDTO> exceptionHandler(BussinesException ex) {

		final var error = new ResponseDTO();

		error.setTransactionDate(new Date());
		error.setResponseCode(HttpStatus.PRECONDITION_FAILED);
		error.setMessage(String.format("BussinesException error: %s", ex.getMessage()));
		error.setTransactionId(tracer.currentSpan().context().traceIdString());

		// Agregar error
		if (ex.getCause() != null) {
			error.setMessage(String.format("BussinesException error: %s", ex.getCause().getMessage()));
		}

		return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(error);

	}

}
