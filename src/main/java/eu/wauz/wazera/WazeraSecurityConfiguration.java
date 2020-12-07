package eu.wauz.wazera;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import eu.wauz.wazera.model.data.auth.UserData;
import eu.wauz.wazera.service.AuthDataService;

@Configuration
@EnableWebSecurity
public class WazeraSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AuthDataService authService;
	
	@Autowired
	private WazeraAuthenticationProvider authProvider;
	
	@Autowired
	private WazeraUserDetailsService detailsService;

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider).userDetailsService(detailsService);
	}
	
	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http.csrf().disable()
			.authorizeRequests()
			.anyRequest().authenticated()
			.and()
			.formLogin()
			.loginProcessingUrl("/perform_login")
			.defaultSuccessUrl("/dashboard.xhtml", true)
			.and()
			.logout()
			.deleteCookies("JSESSIONID")
			.logoutUrl("/perform_logout")
	        .and()
	        .rememberMe();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Component
	private class WazeraAuthenticationProvider implements AuthenticationProvider {
		
		@Override
		public boolean supports(Class<?> authentication) {
			return authentication.equals(UsernamePasswordAuthenticationToken.class);
		}
		
		@Override
		public Authentication authenticate(Authentication authentication) throws AuthenticationException {
			String username = authentication.getName();
	        String password = authentication.getCredentials().toString();
	        if(!authService.authenticate(username, password)) {
	        	throw new BadCredentialsException("Wrong Username or Password!");
	        }
	        return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
		}
		
	}
	
	@Component
	public class WazeraUserDetailsService implements UserDetailsService {

		@Override
		public UserDetails loadUserByUsername(String username) {
			UserData user = authService.findUserByName(username);
			if (user == null) {
				throw new UsernameNotFoundException("Unknown Username!");
			}
			return new UserDetails() {
				
				private static final long serialVersionUID = -6127761950362064659L;

				@Override
				public boolean isEnabled() {
					return true;
				}
				
				@Override
				public boolean isCredentialsNonExpired() {
					return true;
				}
				
				@Override
				public boolean isAccountNonLocked() {
					return true;
				}
				
				@Override
				public boolean isAccountNonExpired() {
					return true;
				}
				
				@Override
				public String getUsername() {
					return user.getUsername();
				}
				
				@Override
				public String getPassword() {
					return user.getPassword();
				}
				
				@Override
				public Collection<? extends GrantedAuthority> getAuthorities() {
					return new ArrayList<>();
				}
				
			};
		}
	}
	
}
