/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgr.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rgr.domain.User;

/**
 *
 * @author tatja
 */
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
