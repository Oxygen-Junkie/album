
package Oxygen.domain;

import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToMany;

/**
 *
 * @author Oxygen-Junkie
 */
@Entity
@Table(name = "photo1")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    private String name;
    private String time;
    private Integer src;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "album_id")
    private Album book;
    
    public Photo() {
        
    }
    
    public Photo(String name, Album album) {
        this.book = album;
        this.name = name;
        this.src = album.getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }
    
    @PrePersist
    public void setTime() {
        Date timestamp = new Date();
        this.time = timestamp.toString();
    }

    public Album getBook() {
        return book;
    }

    public void setBook(Album book) {
        this.book = book;
    }
    
    public Integer getSrc() {
        return src;
    }

    public void setSrc(Integer src) {
        this.src = src;
    }

}
