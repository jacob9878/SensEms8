package com.imoxion.sensems.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6557288008290126838L;

}
