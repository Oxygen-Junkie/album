package Oxygen.domain;

import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author tatja
 */
public enum Role implements GrantedAuthority {

    USER, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
    
}
