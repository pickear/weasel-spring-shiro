package com.weasel.security.exception;

/**
 * 用户不被允许登录该站点异常
 * @author Dylan
 * @time 2013-8-12
 */
public class UnAllowLoginException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6514030193607964171L;
	
	public UnAllowLoginException(){
		super();
	}
	

	public UnAllowLoginException(Throwable e){
		super(e);
	}
	
	public UnAllowLoginException(String message){
		super(message);
	}
}
