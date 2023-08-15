package Oxygen.repos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import Oxygen.domain.Comment;
import Oxygen.domain.Photo;

/**
 *
 * @author tatja
 */
public interface CommentRepo extends CrudRepository<Comment, Long>{
    List<Comment> findByPicture(Photo picture);
    void deleteByPicture(Photo picture);
}
