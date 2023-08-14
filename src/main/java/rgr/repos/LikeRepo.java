/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgr.repos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import rgr.domain.Like;
import rgr.domain.Photo;
/**
 *
 * @author tatja
 */
public interface LikeRepo extends CrudRepository<Like, Long>{
    List <Like> findByImage(Photo image);
    void deleteByImage(Photo image);
}
