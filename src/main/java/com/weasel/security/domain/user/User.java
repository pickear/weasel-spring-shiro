
package com.weasel.security.domain.user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.weasel.core.BaseObject;
import com.weasel.security.helper.PasswordEncoder;
/**
 * @author dylan
 * @email pickear@gmail.com
 * @time 2014年11月24日
 */
public class User  extends BaseObject<Long>{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5394552769206983498L;
	/**
	 * 用户名
	 */
	protected String username;
	/**
	 * 密码
	 */
	protected String password;
	/**
	 * email
	 */
	protected String email;
	/**
	 * 
	 */
	protected String mobilePhone;
    
    /**
     * 帐号保持被锁状态的时间，当前时间超过这个时间说明帐号为非锁状态
     */
	protected String lockedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    /**
     * 角色
     */
	protected Set<Role> roles = new HashSet<Role>();
    /**
     * 权限
     */
    protected Set<Auth> auths = new HashSet<Auth>();
    
    /**
     * 用于转换role
     */
    protected Map<String,Long> roleTrans = new HashMap<String,Long>();
    /**
     * 用于转换auth
     */
    protected Map<String,Long> authTrans = new HashMap<String,Long>();

    public User(){}
    /**
     * Returns the username associated with this user account;
     *
     * @return the username associated with this user account;
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password for this user.
     *
     * @return this user's password
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getLockedTime() {
		return lockedTime;
	}
	public void setLockedTime(String lockedTime) {
		this.lockedTime = lockedTime;
	}
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    
    public Map<String, Long> getRoleTrans() {
		return roleTrans;
	}
    
	public void setRoleTrans(Map<String, Long> roleTrans) {
		this.roleTrans = roleTrans;
	}
	
	public Set<Auth> getAuths() {
		return auths;
	}
	public void setAuths(Set<Auth> auths) {
		this.auths = auths;
	}
	public Map<String, Long> getAuthTrans() {
		return authTrans;
	}
	public void setAuthTrans(Map<String, Long> authTrans) {
		this.authTrans = authTrans;
	}
	public User beforeCreate(){
		translateRole();
		translateAuth();
		return this;
	}
	
	/**
	 * 将装有role id的Map的值注入到role中
	 */
	private void translateRole() {
		Map<String,Long> roleIds = getRoleTrans();
		if(roleIds.isEmpty()){
			return;
		}
		getRoles().clear();
		for(String key : roleIds.keySet()){
			Role role = new Role();
			role.setId(roleIds.get(key));
			getRoles().add(role);
		}
	}
	/**
	 * 将装有auth id的Map的值注入到auth中
	 */
	private void translateAuth(){
		Map<String,Long> authIds = getAuthTrans();
		if(authIds.isEmpty()){
			return;
		}
		getAuths().clear();
		for(String key : authIds.keySet()){
			Auth auth = new Auth();
			auth.setId(authIds.get(key));
			getAuths().add(auth);
		}
	}
	/**
     * @return
     */
    public Set<String> getRolesAsString(){
    	Set<String> roles = new HashSet<String>();
    	for(Role role : getRoles()){
    		roles.add(role.getCode());
    	}
    	return roles;
    }
    /**
     * 
     * @return
     */
    public Set<String> getAuthAsString(){
    	Set<String> auths = new HashSet<String>();
    	for(Auth auth : getAuths()){
    		auths.add(auth.getCode());
    	}
    	return auths;
    }
    
    public boolean hasRoles(){
    	return !getRoles().isEmpty();
    }
    
    public boolean hasAuths(){
    	return !getAuths().isEmpty();
    }
    
    public User encodePassword(){
    	setPassword(PasswordEncoder.encode(getPassword()));
    	return this;
    }
    
	/**
     * 判断用户是否是锁定状态
     * @return
     */
    public boolean isLocked(){
    	Calendar now = Calendar.getInstance();
    	Calendar lockedTime = Calendar.getInstance();
    	try {
			lockedTime.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(getLockedTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return now.before(lockedTime);
    }

}


