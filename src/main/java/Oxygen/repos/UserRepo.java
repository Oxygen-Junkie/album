/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Oxygen.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import Oxygen.domain.User;

/**
 *
 * @author Oxygen-Junkie
 */
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
