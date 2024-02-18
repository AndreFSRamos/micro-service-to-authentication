package tech.andrefsramos.msAuth.exceptions.response;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ExceptionResponse implements Serializable{
	@Serial
	private static final long serialVersionUID = 1L;
	
	private Date timestamp;
	private Integer status;
	private HttpStatus error;
	private String message;
	private String path;
	
	public ExceptionResponse(Date timestamp, HttpStatus error, Integer status, String message, String path) {
		this.setTimestamp(timestamp);
		this.setStatus(status);
		this.setError(error);
		this.setMessage(message);
		this.setPath(path);
	}
}
