/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgr.controller;

import java.io.File;
import java.util.Map;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rgr.domain.Photo;
import rgr.domain.User;
import rgr.repos.CommentRepo;
import rgr.repos.LikeRepo;
import rgr.repos.PhotoRepo;
import rgr.repos.TagRepo;
import rgr.service.MailSender;

/**
 *
 * @author tatja
 */
@Controller
@RequestMapping("/user/admin")
public class AdminController {
    @Autowired 
    private PhotoRepo photoRepo;
    
    @Autowired 
    private CommentRepo commentRepo;
    
    @Autowired 
    private LikeRepo likeRepo;
    
    @Autowired 
    private TagRepo tagRepo;
    
    @Autowired 
    private MailSender mailSender;
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @GetMapping("/album/deletePhoto")
    @Transactional
    public String deletePhoto(@AuthenticationPrincipal User user, @RequestParam Integer id, Map<String, Object> model) {
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
            String message = String.format("Hello " + photo.getBook().getOwner().getUsername() + "! \n" + 
                        "Your photo was considered inapropriate and deleted by " + user.getUsername() + "\n" +
                        "Please respect this site and we will respect content you upload \n" +
                        "Sincerely, photoAlbum administration");
            mailSender.send(photo.getBook().getOwner().getUsername(), "Your photo was removed", message);
            return "redirect:/user/visit/album?id="+src;
        }
        return "redirect:/";
    }
}
