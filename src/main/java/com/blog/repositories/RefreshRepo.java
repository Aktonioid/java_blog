package com.blog.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.blog.models.RefreshModel;

@Repository
public interface RefreshRepo extends MongoRepository<RefreshModel, String> 
{
        
}
