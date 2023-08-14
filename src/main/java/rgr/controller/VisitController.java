/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgr.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rgr.domain.Album;
import rgr.domain.Comment;
import rgr.domain.Photo;
import rgr.domain.User;
import rgr.domain.Friend;
import rgr.domain.Like;
import rgr.domain.Tag;
import rgr.repos.AlbumRepo;
import rgr.repos.CommentRepo;
import rgr.repos.LikeRepo;
import rgr.repos.PhotoRepo;
import rgr.repos.UserRepo;
import rgr.repos.FriendRepo;
import rgr.repos.TagRepo;
import rgr.service.MailSender;


/**
 *
 * @author tatja
 */
@Controller
@RequestMapping("/user/visit")
public class VisitController {
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
    
    @Autowired 
    private MailSender mailSender;
    
    @GetMapping
    public String visit(@AuthenticationPrincipal User user, @RequestParam String username, Map<String, Object> model) {
        List<User> users = userRepo.findAll();
        List list = new ArrayList();
        Boolean check = false;
        for (User temp : users) {                                 // users.remove по какойто причине не работает
            if (!temp.getUsername().equals(user.getUsername())) {
                list.add(temp);
            }
        }
        Iterable<Album> albums = albumRepo.findByOwner(userRepo.findByUsername(username));
        if (user.isPrivilege()) {
            model.put("albums", albums);
        } else {
            List list1 = new ArrayList();
            Iterable<Friend> friends = friendRepo.findByPerson(userRepo.findByUsername(username));
            for(Friend friend : friends) {
                if (friend.getPartner().getUsername().equals(user.getUsername())) {
                    check = true;
                }
            }
            for (Album album : albums) {
                 if (!"Closed".equals(album.getStatus()) && (check || "Opened".equals(album.getStatus()))) {
                     list1.add(album);
                 }
            }
            model.put("albums", list1);
        }
        model.put("userYou", user.getUsername());
        model.put("userHim", username);
        model.put("users", list);
        model.put("friends", friendRepo.findByPerson(user));
        return "somemenu";
    }
    
    @PostMapping
    public String addFriend(@AuthenticationPrincipal User user, @RequestParam String username, Map<String, Object> model) {
        Iterable<Friend> friends = friendRepo.findByPerson(user);
        Boolean check = true;
        for (Friend friend : friends) {
            if (friend.getPartner().getUsername().equals(userRepo.findByUsername(username).getUsername())) {
                check = false; 
            }
        }
        if(check) { 
            Friend friend = new Friend(user, userRepo.findByUsername(username));
            friendRepo.save(friend);
        } else {
            return "redirect:/user/visit?username="+username;
        }
        return "redirect:/user/visit?username="+username;
    }
    
    @GetMapping("/album")
    public String albumNotYours(@AuthenticationPrincipal User user, @RequestParam Integer id, Map<String, Object> model) {
        Album album = albumRepo.findById(id);
        Iterable<Photo> photos = photoRepo.findByBook(album);
        model.put("album1", album);
        model.put("photos", photos);
        if (user.isPrivilege()) {
            return "inspectedalbum";
        } else {
            return "somealbum";
        }
    }
    
    @GetMapping("/album/photo")
    public String openPhoto(@AuthenticationPrincipal User user, @RequestParam Integer id, Map<String, Object> model) {
        Photo photo = photoRepo.findById(id);
        if (photo.getBook().getOwner().getUsername().equals(user.getUsername())) {
            return "redirect:/user/album/photo?id="+id;
        } else {
            Iterable<Comment> comments = commentRepo.findByPicture(photo);
            model.put("comments", comments);
            model.put("photo", photo);
            model.put("count", likeRepo.findByImage(photo).size());        
            return "somephoto";
        }
    }
    
    @PostMapping("/album/photo/addComment")
    public String addComment(@AuthenticationPrincipal User user, @RequestParam Integer id, @RequestParam String text, Map<String, Object> model) {
        if(text != null && !text.isEmpty() && text.length() < 255) {
            Comment comment = new Comment(text, user.getUsername(), photoRepo.findById(id));
            commentRepo.save(comment);
        } else {
            return "redirect:/";
        }
        return "redirect:/user/visit/album/photo?id="+id;
    }
    
    @PostMapping("/album/photo/addLike")
    public String addLike(@AuthenticationPrincipal User user, @RequestParam Integer id, Map<String, Object> model) {
        Iterable<Like> likes = likeRepo.findByImage(photoRepo.findById(id));
        Boolean check = true;
        for (Like like : likes) {
            if (like.getGiver() == user) {
                check = false; 
            }
        }
        if(check) { 
            Like like = new Like(user, photoRepo.findById(id));
            likeRepo.save(like);
        } else {
            return "redirect:/user/visit/album/photo?id="+id; 
        }
        String message = String.format("User " + user.getUsername() + " liked your photo, titled: " + photoRepo.findById(id).getName());
        mailSender.send(photoRepo.findById(id).getBook().getOwner().getUsername(), "Someone liked your photo!", message);
        return "redirect:/user/visit/album/photo?id="+id;
    }
    
    @PostMapping("/album/photo/addTag")
    public String addTag(@AuthenticationPrincipal User user, @RequestParam Integer id, @RequestParam String value, Map<String, Object> model) {
        Iterable<Tag> tags = tagRepo.findByPhoto(photoRepo.findById(id));
        Boolean check = true;
        for (Tag tag : tags) {
            if (tag.getValue().equals(value)) {
                check = false; 
            }
        }
        Tag tag;
        if(check && value.length() < 255 && value != null && !value.isEmpty()) { 
            tag = new Tag(value, photoRepo.findById(id));
            tagRepo.save(tag);
        } else {
            return "redirect:/user/visit/album/photo?id="+id;
        }
        String message = String.format("User " + user.getUsername() + " added tag: " + tag.getValue() + " to your photo, titled: " + photoRepo.findById(id).getName());
        mailSender.send(photoRepo.findById(id).getBook().getOwner().getUsername(), "Someone added tag to your photo!", message);
        return "redirect:/user/visit/album/photo?id="+id;
    }
}
