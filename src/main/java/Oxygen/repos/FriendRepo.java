
package Oxygen.repos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import Oxygen.domain.Friend;
import Oxygen.domain.User;
/**
 *
 * @author Oxygen-Junkie
 */
public interface FriendRepo extends CrudRepository<Friend, Long>{
    List <Friend> findByPerson(User user);
}
