package com.weasel.security.domain.cache;

import java.io.Serializable;
import java.util.Date;

import com.weasel.helper.TimeHelper;

/**
 * @author Dylan
 * @time 2014年3月17日
 */
public class LoginError implements Serializable{

	/**
	 * LoginError.java
	 */
	private static final long serialVersionUID = -2933682505942210599L;
	
	private String username;
	
	private int errorNumber;
	
	private Date validTime = TimeHelper.getCurrentDate();
	
	public String getUsername() {
		return username;
	}

	public LoginError setUsername(String username) {
		this.username = username;
		return this;
	}

	public int getErrorNumber() {
		return errorNumber;
	}

	public LoginError setErrorNumber(int errorNumber) {
		this.errorNumber = errorNumber;
		return this;
	}

	public Date getValidTime() {
		return validTime;
	}

	public LoginError setValidTime(Date validTime) {
		this.validTime = validTime;
		return this;
	}
	
	public LoginError increaseErrorNumber(){
		this.setErrorNumber(this.getErrorNumber()+1);
		return this;
	}
	
	public static LoginError newLoginError(){
		return new LoginError();
	}
	
	

}
