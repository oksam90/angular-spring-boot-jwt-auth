package sn.gainde2000.AngularSpringBootJwtAuth.security.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

//création de la classe AuthEntryPointJwt qui implément l'interface AuthenticationEntryPoint
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
	
	//Jounalisation de la class AuthEntryPointJwt
	private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

	/*
	 * Nous remplaçons la méthode commence()
	 * cette méthode sera déclenchée à chaque fois qu'un utilisateur non authentifié
	 * demande une ressource HTTP sécurisée et une AuthenticationException est levée
	 * */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		logger.error("Erreur non autorisée: {}", authException.getMessage());
		/*
		 * HttpServletResponse.SC_UNAUTHORIZED affcihe le coded'état 401
		 * il indique que la demande nécessite une authentification HTTP
		 * */
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Erreur: non autorisé");
	}

}
