package sn.gainde2000.AngularSpringBootJwtAuth.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * cette classe définit un contrôleur d'autorisation test qui contient 4 API
 * */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
	
	//(api/test/all) pour l'accès public
	@GetMapping("/all")
	public String allAccess() {
		return "Contenu public.";
	}
	
	//(api/test/user) pour les utilisateurs à ROLE_USER ou ROLE_MODERATEUR ou ROLE_ADMIN
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccess() {
		return "Contenu utilisateur.";
	}

	//(api/test/mod) pour les utilisateurs à ROLE_MODERATEUR
	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {
		return "Conseil des modérateurs.";
	}

	//(api/test/admin) pour les utilisateurs à ROLE_ADMIN
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Conseil d'administration.";
	}

}
