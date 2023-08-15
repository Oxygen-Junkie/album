
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
