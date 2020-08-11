package sn.gainde2000.AngularSpringBootJwtAuth.security.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import sn.gainde2000.AngularSpringBootJwtAuth.security.services.UserDetailsImpl;

@Component
public class JwtUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${gainde2000.app.jwtSecret}")
	private String jwtSecret;

	@Value("${gainde2000.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Signature JWT non valide: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Jeton JWT non valide: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("Le jeton JWT a expiré: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("Le jeton JWT n'est pas pris en charge: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("La chaîne de réclamation JWT est vide: {}", e.getMessage());
		}

		return false;
	}

}
