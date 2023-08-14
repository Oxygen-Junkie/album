/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgr.repos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import rgr.domain.Comment;
import rgr.domain.Photo;

/**
 *
 * @author tatja
 */
public interface CommentRepo extends CrudRepository<Comment, Long>{
    List<Comment> findByPicture(Photo picture);
    void deleteByPicture(Photo picture);
}
