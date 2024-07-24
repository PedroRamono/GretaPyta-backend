package com.az.gretapyta.questionnaires.util;

import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.configurationproperties.GeneralProperties;
import com.az.gretapyta.questionnaires.model2.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	@Autowired
	private GeneralProperties generalProperties;

	// retrieve communicable item from jwt token
	public String getSubjectIdentifierFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	//retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
    //for retrieving any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser()
				.setSigningKey(generalProperties.getSecurity().getJwtSecret())
				.parseClaimsJws(token)
				.getBody();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	// If User's login name to be communicated
	public String generateToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, user.getLoginName());
	}

	// User's ID to be communicated
	public String generateTokenWithId(User user) { //AZ808
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, user.getId().toString());
	}

	//while creating the token -
	//1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
	//2. Sign the JWT using the HS512 algorithm and secret key.
	//3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	//   compaction of the JWT to a URL-safe string 
	private String doGenerateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + Constants.JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, generalProperties.getSecurity().getJwtSecret()).compact();
	}

	public Boolean validateTokenByLoginName(String token, User user) {
		final String username = getSubjectIdentifierFromToken(token);
		return (username.equals(user.getLoginName()) && (! isTokenExpired(token)));
	}

	public Boolean validateTokenById(String token, int itemId) {
		final String savedItemId = getSubjectIdentifierFromToken(token);
		return (savedItemId.equals(Integer.toString(itemId)) && (! isTokenExpired(token)));
	}
}