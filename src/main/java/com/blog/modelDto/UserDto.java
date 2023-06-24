package com.blog.modelDto;

import java.util.List;

import com.blog.models.UserRoles;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto 
{
    private String email;
    private String username;
    private String password;
    private List<UserRoles> roles;
        
}
