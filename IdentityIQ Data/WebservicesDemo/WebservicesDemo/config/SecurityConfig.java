package Webservices.WebservicesDemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig 
{
 @Bean
 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
 
 {
   http
	.authorizeHttpRequests(auth-> auth
			.requestMatchers(HttpMethod.GET,"/users_table/**").permitAll()
	        .anyRequest().hasRole("ADMIN")
			)
	.httpBasic()
	.and()
	.csrf().disable();
	return http.build();
 }
 @Bean
 public UserDetailsService users() 
 {
     return new InMemoryUserDetailsManager(
         User.withUsername("Admin")
             .password("{noop}12345")
             .roles("ADMIN")
             .build()
     );
 }
}
