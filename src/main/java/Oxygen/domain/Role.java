package Oxygen.domain;

import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author Oxygen-Junkie
 */
public enum Role implements GrantedAuthority {

    USER, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
    
}
