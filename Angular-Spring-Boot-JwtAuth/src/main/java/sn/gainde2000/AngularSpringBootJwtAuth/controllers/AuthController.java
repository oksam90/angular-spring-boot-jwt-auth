package sn.gainde2000.AngularSpringBootJwtAuth.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sn.gainde2000.AngularSpringBootJwtAuth.models.ERole;
import sn.gainde2000.AngularSpringBootJwtAuth.models.Role;
import sn.gainde2000.AngularSpringBootJwtAuth.models.User;
import sn.gainde2000.AngularSpringBootJwtAuth.payload.request.LoginRequest;
import sn.gainde2000.AngularSpringBootJwtAuth.payload.request.SignupRequest;
import sn.gainde2000.AngularSpringBootJwtAuth.payload.response.JwtResponse;
import sn.gainde2000.AngularSpringBootJwtAuth.payload.response.MessageResponse;
import sn.gainde2000.AngularSpringBootJwtAuth.repository.RoleRepository;
import sn.gainde2000.AngularSpringBootJwtAuth.repository.UserRepository;
import sn.gainde2000.AngularSpringBootJwtAuth.security.jwt.JwtUtils;
import sn.gainde2000.AngularSpringBootJwtAuth.security.services.UserDetailsImpl;
/*
 * Ce contrôleur fournir les api pour les actions d'enrégistrement et de connexion
 * */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	/*
	 * authentifier {username and password}
	 * met à jour SecurityContext à l'aide de l'objet d'authentification
	 * génère JWT
	 * obtenir un UserDetails à partir de l'objet d'authentification
	 * La réponse contient des données JWT et UserDetails
	 * */
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles));
	}

	/*
	 * Vérifie si username et email existe
	 * crée un nouveau User (avec ROLE_USER sinon spécifier le rôle)
	 * enrigistre le User dans la base de données en utilisant UserRepository
	 * */
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Erreur: le nom d'utilisateur est déjà pris!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Erreur: l'e-mail est déjà utilisé!"));
		}

		// Créer un nouveau compte utilisateur
		User user = new User(signUpRequest.getUsername(), 
				 signUpRequest.getEmail(),
				 encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Erreur: le rôle est introuvable."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Erreur: le rôle est introuvable."));
					roles.add(adminRole);

					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("Erreur: le rôle est introuvable."));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Erreur: le rôle est introuvable."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("L'utilisateur s'est enregistré avec succès!"));
	}

}
