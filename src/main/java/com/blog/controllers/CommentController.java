package com.blog.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.models.Comments;
import com.blog.services.CommentService;
import com.blog.services.PostService;

@RestController
@RequestMapping("/comment")
public class CommentController 
{
    @Autowired
    private CommentService service;
    @Autowired
    private PostService postService;
    
    @PostMapping("/create")
    public ResponseEntity<?> createComment(@RequestBody Comments comment)
    {
        Comments com = service.createComment(comment);

        if(com == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return ResponseEntity.ok(com);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<Comments>> getAllCommentsByPostId(String postId)
    {
        try{postService.getPostById(postId);}

        catch(Exception e) {return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
        
        return ResponseEntity.ok(service.getCommentsByPostId(postId));
    } 

    @PostMapping("/delete")
    public ResponseEntity<String> deleteCommentById(String id)
    {
        
        service.deleteCommentById(id);
        return ResponseEntity.ok("");
    }

    @PostMapping("/edit")
    public ResponseEntity<String> editComment(@RequestBody Comments comments)
    {
        service.editComment(comments);
        return ResponseEntity.ok("");
    }
    
    @GetMapping("/get")
    public ResponseEntity<?> getCommentById(String id)
    {
        Comments comment = service.getCommentById(id);
        if(comment == null)return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return ResponseEntity.ok(comment);
    }

    @GetMapping("/test")
    public ResponseEntity<?> testingComment()
    {
        return ResponseEntity.ok("Comment section");
    }
}
