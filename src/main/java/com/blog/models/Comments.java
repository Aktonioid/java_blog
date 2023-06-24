package com.blog.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Document("comment")
public class Comments 
{
    @Id
    private String id;
    private String articleId;
    private String content;
    private String username;
    private Date createdAt;
}
