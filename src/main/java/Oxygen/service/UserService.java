
package Oxygen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import Oxygen.repos.UserRepo;

/**
 *
 * @author Oxygen-Junkie
 */
@Service
public class UserService implements UserDetailsService{
    @Autowired 
    private UserRepo userRepo;
    
    @Autowired 
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }
    
}
