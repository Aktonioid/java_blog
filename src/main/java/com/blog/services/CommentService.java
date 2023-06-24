package com.blog.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blog.models.Comments;
import com.blog.repositories.CommentRepo;
import com.blog.repositories.PostRepo;

@Service
public class CommentService 
{   
    @Autowired
    private CommentRepo repo;
    @Autowired
    private PostRepo postRepo;

    public Comments createComment(Comments comments)
    {
        try
        {
            postRepo.findById(comments.getArticleId()).get();
        }
        catch(Exception e) 
        {
            return null;
        } 
        
        comments.setId(UUID.randomUUID().toString());
        LocalDateTime now = LocalDateTime.now();
        Instant nowInstant = now.atZone(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(nowInstant);
        
        comments.setCreatedAt(date);
        repo.save(comments);

        return comments;
    }

    public List<Comments> getCommentsByPostId(String postId)
    {
        return repo.findByArticleId(postId);
    }

    public List<Comments> getCommentByUser(String username)
    {
        return repo.findByUsername(username);
    }

    public void editComment(Comments comments)
    {
        repo.deleteById(comments.getId());
        repo.save(comments);
    }
    
    public void deleteCommentById(String id)
    {
        repo.deleteById(id);
    }

    public Comments getCommentById(String id)
    {
        return repo.findById(id).get();
    }
}
