package com.blog.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.blog.models.PostModel;

public interface PostRepo extends MongoRepository<PostModel, String>
{
    
}
