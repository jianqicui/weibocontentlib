package org.weibocontentlib.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.weibocontentlib.api.exception.ApiException;

public class BaseApiResource {

	@ExceptionHandler({ ApiException.class })
	public ResponseEntity<String> handleApiException(ApiException apiException) {
		return new ResponseEntity<String>(apiException.getMessage(),
				HttpStatus.valueOf(apiException.getStatusCode()));
	}

}
