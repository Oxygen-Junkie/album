/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Oxygen.repos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import Oxygen.domain.Album;
import Oxygen.domain.Photo;


/**
 *
 * @author tatja
 */
public interface PhotoRepo extends CrudRepository<Photo, Long>{
    List<Photo> findByBook(Album album);
    List<Photo> findByName(String Name);
    Photo findById(Integer id);
    void deleteById(Integer id);
}
