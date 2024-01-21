package com.imoxion.sensems.web.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * role 추가/삭제/초기화/체크 확인할 수 있는 서비스
 * Created by zpqdnjs on 2021-02-16.
 */
@Service
public class RoleService {
	
	/**
	 * role 추가
	 * @param roleName
	 * */
    public void addAuthorites(String roleName){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(authentication.getAuthorities());
        updatedAuthorities.add(new Role(roleName));
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), updatedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }

    /**
     * role 제거
     * @param roleName
     * */
    public void removeAuthorites(String roleName){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(authentication.getAuthorities());
        int deleteIndex = -1;
        for( int i = 0 ; i < updatedAuthorities.size() ; i++ ){
            GrantedAuthority grantedAuthority = updatedAuthorities.get(i);
            if( grantedAuthority.getAuthority().equals( roleName ) ){
                deleteIndex = i;
            }
        }
        if( deleteIndex > -1 ) {
            updatedAuthorities.remove(deleteIndex);
        }
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), updatedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }

    /**
     * role 초기화
     * @return
     * */
    public void clearAuthorites(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> updateAuthorities = new ArrayList<>();
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), updateAuthorities);
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }

    /**
     * 권한 체크. 있으면 return true
     * @param roleName
     * @return
     * */
    public boolean hasRole(String roleName){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if( authentication == null ){
            return false;
        }
        List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
        for(GrantedAuthority role : authorities){
            if( role.getAuthority().equals(roleName) ){
                return true;
            }
        }
        return false;
    }
}
