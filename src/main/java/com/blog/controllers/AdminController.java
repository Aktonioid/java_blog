package com.blog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.models.PostModel;
import com.blog.models.User;
import com.blog.services.PostService;
import com.blog.services.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController
{
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;
    
    @PostMapping("/post/create")
    public ResponseEntity<?> createPost(@RequestBody PostModel post)
    {
        postService.createPost(post);
        return ResponseEntity.ok("");
    }   
    @PostMapping("/edit")
    public ResponseEntity<?> editPost(@RequestBody PostModel model)
    {
        boolean result = postService.editPost(model);
        if(!result) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return ResponseEntity.ok("");
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePostById(String id)
    {
        try{postService.getPostById(id);}
        
        catch(Exception e)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        postService.deletePostById(id);
        return ResponseEntity.ok("");
    }
    
    //
    //User part controller
    // 
    @PostMapping("/user/delete")
    public ResponseEntity<?> deleteUser(String userId)
    {
        userService.deleteUserById(userId);
        return ResponseEntity.ok("");
    }

    @PostMapping("/user/edit")
    public ResponseEntity<?> editUser(User user)
    {
        try{ userService.getUserById(user.getId());}
        catch(Exception e) {return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
        userService.editUser(user);
        return ResponseEntity.ok("");
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> testingAdmin()
    {
        return ResponseEntity.ok("You are admin");
    }
}
