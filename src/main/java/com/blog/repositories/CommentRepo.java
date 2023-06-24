package com.blog.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.blog.models.Comments;

public interface CommentRepo extends MongoRepository<Comments, String>
{
    public List<Comments> findByArticleId(String articleId);
    public List<Comments> findByUsername(String username);
}
