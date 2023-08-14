/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgr.repos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import rgr.domain.Friend;
import rgr.domain.User;
/**
 *
 * @author tatja
 */
public interface FriendRepo extends CrudRepository<Friend, Long>{
    List <Friend> findByPerson(User user);
}
