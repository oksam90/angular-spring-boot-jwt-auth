package sn.gainde2000.AngularSpringBootJwtAuth.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sn.gainde2000.AngularSpringBootJwtAuth.models.User;
import sn.gainde2000.AngularSpringBootJwtAuth.repository.UserRepository;

//la classe UserDetailsServiceImpl implémente UserDetailsService pour obtenir l'objet UserDetails
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	//l'utilisation de UserRepository nous permet d'obtenir l'objet User
	@Autowired
	UserRepository userRepository;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable avec nom d'utilisateur: " + username));

		//en utilisant la méthode statique build on contruit un objet UserDetails
		return UserDetailsImpl.build(user);
	}
}
