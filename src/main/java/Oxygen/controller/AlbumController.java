
package Oxygen.controller;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import jakarta.transaction.TransactionScoped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import Oxygen.domain.Album;
import Oxygen.domain.Comment;
import Oxygen.domain.Photo;
import Oxygen.domain.Tag;
import Oxygen.domain.User;
import Oxygen.repos.AlbumRepo;
import Oxygen.repos.CommentRepo;
import Oxygen.repos.LikeRepo;
import Oxygen.repos.PhotoRepo;
import Oxygen.repos.TagRepo;

/**
 *
 * @author Oxygen-Junkie
 */

@Controller
@RequestMapping("/user/album")
public class AlbumController {
    @Autowired 
    private AlbumRepo albumRepo;
    
    @Autowired 
    private PhotoRepo photoRepo;
    
    @Autowired 
    private CommentRepo commentRepo;
    
    @Autowired 
    private LikeRepo likeRepo;
    
    @Autowired 
    private TagRepo tagRepo;
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @GetMapping
    public String photo(@RequestParam Integer id, Map<String, Object> model) {
        Album album = albumRepo.findById(id);
        Iterable<Photo> photos = photoRepo.findByBook(album);
        model.put("album1", album);
        model.put("photos", photos);
        return "album";
    }
    
    @PostMapping("/addPhoto")
    public String addPhoto(@RequestParam Integer id, @RequestParam("file") MultipartFile file, Map<String, Object> model) {
        Album album = albumRepo.findById(id);
        Iterable<Photo> photos = photoRepo.findByBook(album);
        Boolean check = true;
        try {
            for (Photo pic : photos) {
                if (pic.getName().equals(file.getOriginalFilename())) {
                    check = false;
                }
            }
            if (check) {
                String ourUploadPath = uploadPath + File.separator + album.getId() + File.separator;
                if (!(new File(ourUploadPath)).exists()) {
                    (new File(ourUploadPath)).mkdirs();
                }
                ourUploadPath += file.getOriginalFilename();
                file.transferTo(new File(ourUploadPath));
                if (ImageIO.read(new File(ourUploadPath)) == null) {
                    File temp = new File(ourUploadPath);
                    temp.delete();
                    return "redirect:/user/album?id="+id;
                }
                Photo photo = new Photo(file.getOriginalFilename(), album);
                photo.setTime();
                photoRepo.save(photo);
                return "redirect:/user/album?id="+id;
            } else {
                return "redirect:/user/album?id="+id;
            }
        } catch (IOException | IllegalStateException ex) {
            Logger.getLogger(AlbumController.class.getName()).log(Level.SEVERE, null, ex);
            return "redirect:/user/album?id="+id;
        }
    }
    
    @GetMapping("/deletePhoto")
    @TransactionScoped
    public String deletePhoto(@RequestParam Integer id, Map<String, Object> model) {
        if (photoRepo.findById(id) != null) {
            Photo photo = photoRepo.findById(id);
            commentRepo.deleteByPicture(photo);
            likeRepo.deleteByImage(photo);
            tagRepo.deleteByPhoto(photo);
            Integer src = photo.getSrc();
            String ourDeletePath = uploadPath + File.separator + src + File.separator + photo.getName();
            File temp = new File(ourDeletePath);
            temp.delete();
            photoRepo.deleteById(id);
            return "redirect:/user/album?id="+src;
        }
        return "redirect:/";
    }
    
    @GetMapping("/photo")
    public String openPhoto(@RequestParam Integer id, Map<String, Object> model) {
        Photo photo = photoRepo.findById(id);
        Iterable<Comment> comments = commentRepo.findByPicture(photo);
        model.put("comments", comments);
        model.put("photo", photo);
        model.put("count", likeRepo.findByImage(photo).size());
        model.put("tags", tagRepo.findByPhoto(photo));
        return "photo";
    }
    
    @PostMapping("/photo/addComment")
    public String addComment(@AuthenticationPrincipal User user, @RequestParam Integer id, @RequestParam String text, Map<String, Object> model) {
        if(text != null && !text.isEmpty() && text.length() < 255) {
            Comment comment = new Comment(text, user.getUsername(), photoRepo.findById(id));
            commentRepo.save(comment);
        } else {
            return "redirect:/";
        }
        return "redirect:/user/album/photo?id="+id;
    }
    
    @PostMapping("/photo/addTag")
    public String addTag(@AuthenticationPrincipal User user, @RequestParam Integer id, @RequestParam String value, Map<String, Object> model) {
        Iterable<Tag> tags = tagRepo.findByPhoto(photoRepo.findById(id));
        Boolean check = true;
        for (Tag tag : tags) {
            if (tag.getValue().equals(value)) {
                check = false; 
            }
        }
        if(check && value.length() < 255 && value != null && !value.isEmpty()) { 
            Tag tag = new Tag(value, photoRepo.findById(id));
            tagRepo.save(tag);
        } else {
            return "redirect:/user/visit/album/photo?id="+id;
        }
        return "redirect:/user/visit/album/photo?id="+id;
    }
}
