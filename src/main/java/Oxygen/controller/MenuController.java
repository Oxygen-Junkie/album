
package Oxygen.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import Oxygen.domain.Album;
import Oxygen.domain.Friend;
import Oxygen.domain.Photo;
import Oxygen.domain.Tag;
import Oxygen.domain.User;
import Oxygen.repos.AlbumRepo;
import Oxygen.repos.CommentRepo;
import Oxygen.repos.LikeRepo;
import Oxygen.repos.PhotoRepo;
import Oxygen.repos.UserRepo;
import jakarta.transaction.TransactionScoped;
import Oxygen.repos.FriendRepo;
import Oxygen.repos.TagRepo;

@Controller
@RequestMapping("/user")
public class MenuController {
    @Autowired 
    private UserRepo userRepo;
    
    @Autowired 
    private AlbumRepo albumRepo;
    
    @Autowired 
    private PhotoRepo photoRepo;
    
    @Autowired 
    private CommentRepo commentRepo;
    
    @Autowired 
    private LikeRepo likeRepo;
    
    @Autowired 
    private FriendRepo friendRepo;
    
    @Autowired 
    private TagRepo tagRepo;
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @GetMapping
    public String action(@AuthenticationPrincipal User user, Map<String, Object> model) {
        Iterable<User> users = userRepo.findAll();
        List<User> list = new ArrayList<User>();
        for (User temp : users) {                                 // users.remove по какойто причине не работает
            if (!temp.getUsername().equals(user.getUsername()) ) {
                list.add(temp);
            }
        }
        model.put("albums", albumRepo.findByOwner(user));
        model.put("user", user.getUsername());
        model.put("users", list);
        model.put("friends", friendRepo.findByPerson(user));
        return "menu";
    }
    
    @PostMapping("/addAlbum")
    public String addAlbum(@AuthenticationPrincipal User user, @RequestParam String title, @RequestParam String status, Map<String, Object> model) {
        if(title != null && !title.isEmpty() && title.length() < 255) {
            Album album = new Album(title, status, user);
            albumRepo.save(album);
        } else {
            return "errorMenu";
        }
        return "redirect:/user";
    }
    
    @GetMapping("/deleteAlbum")
    @TransactionScoped
    public String deleteAlbum(@AuthenticationPrincipal User user, @RequestParam Integer id, Map<String, Object> model) {
        if (albumRepo.findById(id) != null) {
            Album album = albumRepo.findById(id);
            if (photoRepo.findByBook(album) != null) {
                Iterable<Photo> photos = photoRepo.findByBook(album);
                for (Photo photo : photos) {
                    commentRepo.deleteByPicture(photo);
                    likeRepo.deleteByImage(photo);
                    tagRepo.deleteByPhoto(photo);
                    Integer photoId = photo.getId();
                    String ourDeletePath = uploadPath + File.separator + id + File.separator + photo.getName();
                    File temp = new File(ourDeletePath);
                    temp.delete();
                    photoRepo.deleteById(photoId);
                }
            }
            if ((new File(uploadPath + File.separator + id)).exists()) {
                File temp = new File(uploadPath + File.separator + id);
                temp.delete();
            }
            albumRepo.deleteById(id);
        }
        return "redirect:/user";
    }
    
    @PostMapping("/find")
    public String search(@AuthenticationPrincipal User user, @RequestParam String request, Map<String, Object> model) {
        if(request != null && !request.isEmpty() && request.length() < 255) {
            Boolean myOwn, albCheck;
            Iterable<Album> albums = albumRepo.findByOwner(userRepo.findByUsername(request));
            List<Photo> photos1 = new ArrayList<Photo>();
            Boolean priv = user.isPrivilege();
            for (Album album : albums) {
                myOwn = userRepo.findByUsername(request).getUsername().equals(user.getUsername());
                albCheck = !"Closed".equals(album.getStatus()) && (ifFriend(user, userRepo.findByUsername(request)) || "Opened".equals(album.getStatus()));
                if (priv || myOwn || albCheck) {
                    photos1.addAll(photoRepo.findByBook(album));
                }
            }
            model.put("photos1", photos1);
            Iterable<Photo> list = photoRepo.findByName(request);
            List<Photo> photos2 = new ArrayList<Photo>();
            for (Photo photo : list) {
                Album album = photo.getBook();
                myOwn = album.getOwner().getUsername().equals(user.getUsername());
                albCheck = !"Closed".equals(album.getStatus()) && (ifFriend(user, album.getOwner()) || "Opened".equals(album.getStatus()));
                if (priv || myOwn || albCheck) {
                    photos2.add(photo);
                }
            }
            model.put("photos2", photos2);
            List<Photo> photos3 = new ArrayList<Photo>();
            Iterable<Tag> tags = tagRepo.findByValue(request);
            for (Tag tag : tags) {
                Album album = tag.getPhoto().getBook();
                myOwn = album.getOwner().getUsername().equals(user.getUsername());
                albCheck = !"Closed".equals(album.getStatus()) && (ifFriend(user, album.getOwner()) || "Opened".equals(album.getStatus()));
                if (priv || myOwn || albCheck) {
                    photos3.add(tag.getPhoto());
                }
            }
            model.put("photos3", photos3);
        } else {
            return "errorMenu";
        }
        return "searchResponce";
    }
    
    public Boolean ifFriend(User user1, User user2) {
        Iterable<Friend> friends = friendRepo.findByPerson(user2);
        for(Friend friend : friends) {
            if (friend.getPartner().getUsername().equals(user1.getUsername())) {
                return true;
            }
        }
        return false;
    }
    
}