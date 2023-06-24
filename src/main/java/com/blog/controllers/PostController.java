package com.blog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.services.PostService;

@RestController
@RequestMapping("/article")
public class PostController 
{
    @Autowired
    private PostService service;
    
    @GetMapping()
    public ResponseEntity<?> getAllPosts()
    {
        return ResponseEntity.ok(service.getAllPosts());
    }


    @GetMapping("/post")
    public ResponseEntity<?> getPostById(String id)
    {
        try
        {
            return ResponseEntity.ok(service.getPostById(id));
        }
        
        catch(Exception e)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
