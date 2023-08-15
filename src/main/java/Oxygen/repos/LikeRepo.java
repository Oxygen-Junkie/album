
package Oxygen.repos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import Oxygen.domain.Like;
import Oxygen.domain.Photo;
/**
 *
 * @author Oxygen-Junkie
 */
public interface LikeRepo extends CrudRepository<Like, Long>{
    List <Like> findByImage(Photo image);
    void deleteByImage(Photo image);
}
