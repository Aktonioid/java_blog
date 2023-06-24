package com.blog.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.blog.models.LogInModel;
import com.blog.models.RefreshModel;
import com.blog.models.User;
import com.blog.repositories.UserRepo;
import com.blog.services.JwtService;
import com.blog.services.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class UserController //login, registration, logout
{
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody User user, HttpServletResponse response)
    {
        if(userService.registration(user) == false)
        {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        user = userService.createUser(user);

        List<String> refreshTokens;

        final String accessToken = jwtService.generateAccessToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);

    
        refreshTokens= new ArrayList<String>();
        refreshTokens.add(refreshToken);
            
        RefreshModel model = new RefreshModel();
            
        model.setId(user.getId());
        model.setRefreshList(refreshTokens);
 
        jwtService.createRefreshModel(model);

        Cookie accessCookie = new Cookie("access", accessToken);
        accessCookie.setHttpOnly(true);

        Cookie refreshCookie = new Cookie("refresh", refreshToken);
        refreshCookie.setHttpOnly(true);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/login")
    public ResponseEntity<?> logIn(@RequestBody LogInModel model, HttpServletResponse response)
    {
        if(userService.logIn(model) == false)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = userService.findUserByUsername(model);

        List<String> refreshTokens;
        
        final String accessToken = jwtService.generateAccessToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);

        try
        {
            refreshTokens = jwtService.getRefreshListById(user.getId());
            RefreshModel refreshModel = new RefreshModel();
            refreshModel.setId(user.getId());
            refreshModel.setRefreshList(refreshTokens);
            jwtService.editRefreshModel(refreshModel);
        }
        catch(Exception e)
        {
            refreshTokens= new ArrayList<String>();
            refreshTokens.add(refreshToken);
            
            RefreshModel refresmModel = new RefreshModel();
            
            refresmModel.setId(user.getId());
            refresmModel.setRefreshList(refreshTokens);
            jwtService.createRefreshModel(refresmModel);
        }

        Cookie accessCookie = new Cookie("access", accessToken);
        accessCookie.setHttpOnly(true);

        Cookie refreshCookie = new Cookie("refresh", refreshToken);
        refreshCookie.setHttpOnly(true);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
        
        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logOut(HttpServletResponse response, HttpServletRequest request)
    {
        Cookie deleteRefresh = jwtService.getCookieByName("refresh", request);

        //delete refresh token from database
        List<String> refreshList;
        RefreshModel model = new RefreshModel();
        User user;
        if(deleteRefresh != null)
        {
            user = userService.findUserByUsername(jwtService.getUsername(deleteRefresh.getValue()));
            refreshList = jwtService.getRefreshListById(user.getId());
            refreshList.remove(deleteRefresh.getValue());
            model.setId(user.getId());
            model.setRefreshList(refreshList);
            jwtService.editRefreshModel(model);
        }

        //end delete
        Cookie accessCookie = new Cookie("access", "");
        accessCookie.setHttpOnly(true);
        accessCookie.setMaxAge(0);

        Cookie refreshCookie = new Cookie("refresh", "");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(0);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok("You've been logged out");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refershTokens(HttpServletRequest request, HttpServletResponse response)
    {
        User user = jwtService.getUserByToken(request);

        Cookie refreshRequestCookie = jwtService.getCookieByName("refresh", request);
        if(user == null || refreshRequestCookie == null) 
        {
            Cookie accessCookie = new Cookie("access", "");
            accessCookie.setHttpOnly(true);
            accessCookie.setMaxAge(0);

            Cookie refreshCookie = new Cookie("refresh", "");
            refreshCookie.setHttpOnly(true);
            refreshCookie.setMaxAge(0);

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); //в токене записан не правильный username
        }
        
        if(!jwtService.isTokenExpired(refreshRequestCookie.getValue())) 
        {   
            Cookie accessCookie = new Cookie("access", "");
            accessCookie.setHttpOnly(true);
            accessCookie.setMaxAge(0);

            Cookie refreshCookie = new Cookie("refresh", "");
            refreshCookie.setHttpOnly(true);
            refreshCookie.setMaxAge(0);

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        List<String> refreshList = jwtService.getRefreshListById(user.getId());
        refreshList.remove(refreshRequestCookie.getValue());
        refreshList.add(refresh);

        RefreshModel model = new RefreshModel();
        model.setId(user.getId());
        model.setRefreshList(refreshList);

        Cookie accessCookie = new Cookie("access", access);
        accessCookie.setHttpOnly(true);

        Cookie refreshCookie = new Cookie("refresh", refresh);
        refreshCookie.setHttpOnly(true);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
        

        return ResponseEntity.ok(access);
    }

    @GetMapping("/test")
    public ResponseEntity<?> getAllCookies(HttpServletRequest request)
    {
        Cookie[] cookies = request.getCookies();
        Cookie returnable = null;
        for (Cookie cookie : cookies) 
        {
            if(cookie.getName().equals("refresh"))
            {
                returnable = cookie;
            }    
        }
        
        return ResponseEntity.ok(returnable);
    }
}
