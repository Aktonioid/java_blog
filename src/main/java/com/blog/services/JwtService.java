package com.blog.services;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.blog.models.RefreshModel;
import com.blog.models.User;
import com.blog.repositories.RefreshRepo;
import com.blog.repositories.UserRepo;
import com.mongodb.lang.NonNull;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtService 
{
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RefreshRepo refreshRepo;

    private final String accessSecret;
    private final String refreshSecret;

    public JwtService (@Value("${jwt.secret.access}") String accessSecret,
                       @Value("${jwt.secret.access}") String refrshSecret)
    {
        this.refreshSecret = refrshSecret;
        this.accessSecret = accessSecret;
    }

    public String generateAccessToken(@NonNull User user)
    {
        final LocalDateTime now = LocalDateTime.now();
        final Instant instantExpired = now.plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant();
        final Date expiredDate = Date.from(instantExpired);

        final String token =Jwts.builder()
                                .setSubject(user.getUsername())
                                .setIssuedAt(expiredDate)
                                .claim("roles", user.getRole())
                                .claim("username", user.getUsername())
                                .claim("email", user.getEmail())
                                .signWith(accessSecretKey(), SignatureAlgorithm.HS256)
                                .compact();

        return token;
    }

    public String generateRefreshToken(@NonNull User user)
    {
        final LocalDateTime now =  LocalDateTime.now();
        final Instant instantExpired = now.plusDays(4).atZone(ZoneId.systemDefault()).toInstant();
        final Date expiredDate = Date.from(instantExpired);

        final String token = Jwts.builder()
                                 .setSubject(user.getUsername())
                                 .setIssuedAt(expiredDate)
                                 .claim("username", user.getUsername())
                                 .signWith(refreshSecretKey(), SignatureAlgorithm.HS256)
                                 .compact();

        return token;
    }

    public User getUserByToken(HttpServletRequest request)
    {
        final String header = request.getHeader("Authorization");
        System.out.println(header);
        if(header == null || !header.startsWith("Bearer")) return null;

        String token = header.substring(7);

        String username = getUsername(token);
        System.out.println(username);
        return userRepo.findUserByUsername(username);
    }

    private final Key accessSecretKey()
    {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
    }

    private final Key refreshSecretKey()
    {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
    }

    private <T> T getClaim(String token, Function<Claims, T> calimsResolver)
    {
        final Claims calims = getAllClaims(token);
        return calimsResolver.apply(calims);
    }

    private Claims getAllClaims(String token)
    {
        return Jwts.parserBuilder().setSigningKey(accessSecretKey()).build().parseClaimsJws(token).getBody();
    }

    private Date tokenExpiration(String token)
    {
        return getClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token)
    {
        return tokenExpiration(token).before(new Date());
    }


    public String getUsername(String token)
    {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token,  UserDetails user)
    {
        final String extractedUsername = getUsername(token);
        return (user.getUsername().equals(extractedUsername)) && (!isTokenExpired(token));
    }

    public List<String> getRefreshListById(String id)
    {
        return refreshRepo.findById(id).get().getRefreshList(); 
    }

    public void createRefreshModel(RefreshModel model)
    {
        refreshRepo.save(model);
    }

    public boolean editRefreshModel(RefreshModel model)
    {
        try{getRefreshListById(model.getId());}
        catch(Exception e){ return false;}

        refreshRepo.deleteById(model.getId());
        refreshRepo.save(model);

        return true;
    }

    public Cookie getCookieByName(String name, HttpServletRequest request)
    {
        for(Cookie cookie : request.getCookies())
        {
            if(cookie.getName().equals(name))
            {
                return cookie;
            }
        }
        return null;
    }

}
