package org.weibocontentlib.api.exception;

public class ApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private int statusCode;

	public int getStatusCode() {
		return statusCode;
	}

	public ApiException(Throwable cause, int statusCode) {
		super(cause);

		this.statusCode = statusCode;
	}

}
