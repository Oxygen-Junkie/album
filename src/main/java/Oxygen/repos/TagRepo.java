/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Oxygen.repos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import Oxygen.domain.Photo;
import Oxygen.domain.Tag;

/**
 *
 * @author tatja
 */
public interface TagRepo extends CrudRepository<Tag, Long>{
    List<Tag> findByValue(String value);
    List<Tag> findByPhoto(Photo photo);
    void deleteByPhoto(Photo photo);
}
