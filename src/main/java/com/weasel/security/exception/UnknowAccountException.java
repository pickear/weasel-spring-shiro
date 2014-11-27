package com.weasel.security.exception;

/**
 * 用户同时在线异常
 * @author Dylan
 * @time 2013-8-12
 */
public class UnknowAccountException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7510980789927862457L;

	public UnknowAccountException(){
		super();
	}
	public UnknowAccountException(String message){
		super(message);
	}
}
