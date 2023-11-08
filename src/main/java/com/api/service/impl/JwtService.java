package com.api.service.impl;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
@Service
public class JwtService {
	
	
	@Value("${jwt.secret}")
	 private String SECRET ;

	 @Value("${jwt.expires_in}")
	    private int EXPIRES_IN;
	 
	  public String extractUsername(String token) {
	        return extractClaim(token, Claims::getSubject);
	    }

	    public Date extractExpiration(String token) {
	        return extractClaim(token, Claims::getExpiration);
	    }

	    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
	        final Claims claims = extractAllClaims(token);
	        return claimsResolver.apply(claims);
	    }

	    private Claims extractAllClaims(String token) {
	    	 try {
	        return Jwts
	                .parserBuilder()
	                .setSigningKey(getSignKey())
	                .build()
	                .parseClaimsJws(token)
	                .getBody();
	    	 } catch (Exception e) {
	                throw new BadCredentialsException("Invalid Token received!");
	            }
	    }

	    private Boolean isTokenExpired(String token) {
	    	try {
	        return extractExpiration(token).before(new Date());
	    	 } catch (Exception e) {
	                throw new BadCredentialsException("Expired Token received!");
	            }
	    }

	    public String generateToken(String email, String mobileNumber){
	        Map<String,Object> claims = new HashMap<>();
	        return createToken(claims, email, mobileNumber);
	    }
	        
	    private String createToken(Map<String, Object> claims, String email, String mobileNumber) {
	        return Jwts.builder()
	                .setIssuer("Dailybook")
	                
	                .setClaims(claims)
	                .setSubject(email) 
	                .setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(generateExpirationDate())
	                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	    }


	    private String createToken(Map<String, Object> claims, String email, String mobileNumber, Date expirationDate) {
	        return Jwts.builder()
	                .setIssuer("Dailybook")
	                .setClaims(claims)
	                .setSubject(email)
	                .setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(expirationDate) // Set the expiration date here
	                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	    }

	    private Key getSignKey() {
	        byte[] keyBytes= Decoders.BASE64.decode(SECRET);
	        return Keys.hmacShaKeyFor(keyBytes);
	    }

	    public String generateExpiredToken() {
	        Map<String, Object> claims = new HashMap<>();
	        return createToken(claims, "", "", new Date(0)); // Token with expiration in the past
	    }

	    private List<String> revokedTokens = new ArrayList<>();

	    public void revokeToken(String token) {
	        revokedTokens.add(token);
	    }
	    
//	    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !isTokenRevoked(token));
    }



    private Boolean isTokenRevoked(String token) {
        return revokedTokens.contains(token);
    }


    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + this.EXPIRES_IN * 10000);
    }
}
