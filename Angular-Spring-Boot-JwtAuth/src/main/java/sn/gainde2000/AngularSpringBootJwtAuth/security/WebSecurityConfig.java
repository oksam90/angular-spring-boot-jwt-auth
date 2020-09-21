package sn.gainde2000.AngularSpringBootJwtAuth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import sn.gainde2000.AngularSpringBootJwtAuth.security.jwt.AuthEntryPointJwt;
import sn.gainde2000.AngularSpringBootJwtAuth.security.jwt.AuthTokenFilter;
import sn.gainde2000.AngularSpringBootJwtAuth.security.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity //permet a spring de trouver et d'appliquer auto la class à la sécu web global
@EnableGlobalMethodSecurity(
							//securedEnabled = true,
							//jsr250Enabled = true,
							prePostEnabled = true) // @EnableGlobalMethodSecurity fournit une sécurité AOP sur la méthode
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	
	@Autowired
	UserDetailsServiceImpl userDetailsService;
	
	//Gestion des exceptions 
	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;
	
	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}
	
	/*
	 * pour que spring sécurité les détails du user pour effectuer l'auth et l'autorisation
	 * on doit implémenter l'interface UserDetailsService, il sera utiliser pour congufirer DaoAuthticationProvider
	 * par la méthode authenticationManagerBuilder.userDetailsService
	 * */
	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Bean
	public AuthenticationManager authenticationManangerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	//pour le cryptage du mot de passe, on évite qu'il soit du text Brute
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	//Configuration de cors et csrf pour exiger l'auth des users ou pas
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable()
			.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.authorizeRequests().antMatchers("/api/auth/**").permitAll()
			.antMatchers("/api/test/**").permitAll()
			.antMatchers("/api/**").permitAll()
			.anyRequest().authenticated();

		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}
}
