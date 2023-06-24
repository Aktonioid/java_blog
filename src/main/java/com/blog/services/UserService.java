package com.blog.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.blog.models.LogInModel;
import com.blog.models.User;
import com.blog.repositories.UserRepo;

@Service
public class UserService 
{
    @Autowired
    private UserRepo repo;
    @Autowired
    private PasswordEncoder encoder;
    
    public User createUser(User user)
    {
        user.setId(UUID.randomUUID().toString());
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);
        return user;
    }

    public void editUser(User user)
    {
        repo.deleteById(user.getId());
        repo.save(user);
    }

    public void deleteUserById(String id)
    {
        repo.deleteById(id);
    }

    public User getUserById(String id)
    {
        return repo.findById(id).get();
    }

    public List<User> getAllUsers()
    {
        return repo.findAll();
    }

    public boolean logIn(LogInModel login)
    {
        User user = repo.findUserByUsername(login.getUsername());

        if((!encoder.matches(login.getPassword(), user.getPassword()))||(user == null))
        {
            return false;//404 error if null
        }
        return true;
    }

    public boolean registration(User user)
    {
        if(repo.findUserByUsername(user.getUsername()) != null || (repo.findUserByEmail(user.getEmail()))!= null)
        {
            return false; //user with such username or email exists
        }
        return true;
    }

    public User findUserByUsername(LogInModel model)
    {
        return repo.findUserByUsername(model.getUsername());
    }

    public User findUserByUsername(String username)
    {
        return repo.findUserByUsername(username);
    }
}
