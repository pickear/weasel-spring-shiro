package com.weasel.security.exception;

/**
 * @author Dylan
 * @time 2013-8-12
 */
public class InvalidCaptchaException extends RuntimeException {

	

	/**
	 * InvalidCaptchaException.java
	 */
	private static final long serialVersionUID = -5152862908818792764L;
	
	public InvalidCaptchaException(){
		super();
	}
	public InvalidCaptchaException(String message){
		super(message);
	}
}
