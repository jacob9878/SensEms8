package com.imoxion.sensems.web.authentication;

import org.springframework.security.core.GrantedAuthority;

/**
 * Created by zpqdnjs on 2021-02-16.
 * 권한 객체
 */
public class Role implements GrantedAuthority {

    private String name;

    public Role(){}

    public Role(String name){
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
