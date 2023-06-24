package com.blog.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document("refersh")
@Getter
@Setter
public class RefreshModel
{
    @Id
    private String id;

    private List<String> refreshList;
}
