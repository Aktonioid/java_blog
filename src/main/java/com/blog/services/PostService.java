package com.blog.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blog.models.PostModel;
import com.blog.repositories.PostRepo;

@Service
public class PostService
{
    @Autowired
    private PostRepo repo;
    
    public void createPost(PostModel model)
    {
        model.setId(UUID.randomUUID().toString());
        repo.save(model);
    }

    public void deletePostById(String id)
    {
        repo.deleteById(id);
    }

    public PostModel getPostById(String id)
    {
        return repo.findById(id).get();
    }

    public List<PostModel> getAllPosts()
    {
        return repo.findAll();
    }

    public boolean editPost(PostModel model)
    {

        try{getPostById(model.getId()); }
        catch(Exception e)
        {
            return false;
        }

        deletePostById(model.getId());
        repo.save(model);

        return true;
    }
    
}
