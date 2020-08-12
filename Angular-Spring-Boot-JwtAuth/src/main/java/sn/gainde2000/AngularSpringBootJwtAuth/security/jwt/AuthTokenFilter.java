package sn.gainde2000.AngularSpringBootJwtAuth.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import sn.gainde2000.AngularSpringBootJwtAuth.security.services.UserDetailsServiceImpl;

/*
 * la classe AuthTokenFilter éntend OncePerRequestFilter et remplace la méthode doFilterInternal
 * et s'execute une fois par requête*/
public class AuthTokenFilter extends OncePerRequestFilter{
	
	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	//Journalisation de la class AuthTokenFilter 
	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	
	/*
	 * dans le doFilterInternal:
	 * obtenir jwt de l'en-tête d'autorisation(en supprimant le préfixe Bearer)
	 * Si la requête a JWT validez-la, analysez le nom d'utilisateur à partir de celle-ci
	 * à partir du nom d'utilisateur, récupérer UserDatails pour créer un objet d'autehtification
	 * Définir les UserDetails actuels dans SecurityContext à l'aide de la méthode setAuthentication (authentification)
	 * 
	 * */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = parseJwt(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				String username = jwtUtils.getUserNameFromJwtToken(jwt);

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			logger.error("Impossible de définir l'authentification utilisateur: {}", e);
		}

		filterChain.doFilter(request, response);
	}

	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}

		return null;
	}

}
