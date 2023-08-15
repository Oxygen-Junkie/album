
package Oxygen.repos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import Oxygen.domain.Photo;
import Oxygen.domain.Tag;

/**
 *
 * @author Oxygen-Junkie
 */
public interface TagRepo extends CrudRepository<Tag, Long>{
    List<Tag> findByValue(String value);
    List<Tag> findByPhoto(Photo photo);
    void deleteByPhoto(Photo photo);
}
