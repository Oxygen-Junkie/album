
package Oxygen.repos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import Oxygen.domain.Album;
import Oxygen.domain.Photo;


/**
 *
 * @author Oxygen-Junkie
 */
public interface PhotoRepo extends CrudRepository<Photo, Long>{
    List<Photo> findByBook(Album album);
    List<Photo> findByName(String Name);
    Photo findById(Integer id);
    void deleteById(Integer id);
}
