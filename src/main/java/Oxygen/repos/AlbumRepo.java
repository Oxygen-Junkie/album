
package Oxygen.repos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import Oxygen.domain.Album;
import Oxygen.domain.User;

/**
 * 
 * @author Oxygen-Junkie
 */
public interface AlbumRepo extends CrudRepository<Album, Long>{
    List<Album> findByOwner(User user);
    Album findByTitle(String title);
    Album findById(Integer id);
    void deleteById(Integer id);
    
    
}
