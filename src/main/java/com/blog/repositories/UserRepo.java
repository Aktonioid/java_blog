package com.blog.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.blog.models.User;

public interface UserRepo extends MongoRepository<User, String>
{
    public User findUserByUsername(String username);
    public User findUserByEmail(String email);
}
