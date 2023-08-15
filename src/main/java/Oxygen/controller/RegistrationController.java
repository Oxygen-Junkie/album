/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Oxygen.controller;

import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import Oxygen.domain.Role;
import Oxygen.domain.User;
import Oxygen.repos.UserRepo;

/**
 *
 * @author Oxygen-Junkie
 */
@Controller
public class RegistrationController {
    
    @Autowired 
    private UserRepo userRepo;
    
    @Autowired 
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }
    
    @PostMapping("/registration")
    public String addUser(User user, Map<String, Object> model) {
        if (user.getUsername().contains("@") && user.getUsername().contains(".") && user.getUsername()!=null && user.getUsername().length() < 255 && user.getPassword().length() < 255) {} else {
            model.put("message", "Incorrect form of an Email");
            return "registration";
        }
        
        if (userRepo.findByUsername(user.getUsername()) != null) {
            model.put("message", "User exists!");
            return "registration";
        }
        
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPrivilege(false);
        userRepo.save(user);
        
        return "redirect:/login";
    }
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/error")
    public String error() {
        return "redirect:/index";
    }
}
