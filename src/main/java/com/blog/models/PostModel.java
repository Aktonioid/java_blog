package com.blog.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document("posts")
@Getter
@Setter
public class PostModel // model for article 
{
    @Id
    private String id;
    private String content; // Содержимое поста
    private String title; //заголовок поста     
}
