/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.weasel.security.domain.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.weasel.core.BaseObject;

/**
 * @author Dylan
 * @time 2013-8-5
 */
public class Role extends BaseObject<Long>{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6769720272431073142L;
	
	private String code;

	private String name;

    private Set<Auth> auths = new HashSet<Auth>();
    
    private Map<String,Long> authTrans = new HashMap<String,Long>();
    
    public Role(){}

    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
   
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public Role beforeCreate(){
		translateAuths();
		return this;
	}

	private void translateAuths() {
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
	public Set<String> getAuthsAsString(){
		Set<String> auths = new HashSet<String>();
		for(Auth auth : getAuths()){
			auths.add(auth.getCode());
		}
		return auths;
	}
	
	public boolean hasAuth(){
		return !getAuths().isEmpty();
	}

}


