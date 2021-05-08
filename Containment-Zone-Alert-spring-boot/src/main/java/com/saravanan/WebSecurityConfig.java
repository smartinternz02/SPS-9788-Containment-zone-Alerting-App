package com.saravanan;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.saravanan.filters.JwtRequestFilter;
import com.saravanan.models.MyUserDetails;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private MyUserDetailService myUserDetailsService;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(myUserDetailsService);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Configuration
	@Order(1)
	public static class AdminConfigurationAdapter extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {

			http.
					antMatcher("/admin/**").authorizeRequests().anyRequest()
					.hasAuthority("ADMIN").and().exceptionHandling().accessDeniedPage("/admin/error/403").and()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS).and().formLogin()
					.usernameParameter("email").defaultSuccessUrl("/admin/home").permitAll().and().logout().permitAll();
		}

	}

	@Configuration
	@Order(2)
	public static class UserConfigurationAdapter extends WebSecurityConfigurerAdapter {
		@Autowired
		private JwtRequestFilter jwtRequestFilter;

		@Override
		protected void configure(HttpSecurity httpSecurity) throws Exception {

			httpSecurity.csrf().disable().
					antMatcher("/user/**").authorizeRequests().antMatchers("/user/authenticate", "/user/register")
					.permitAll().anyRequest().hasAuthority("USER").and().sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().formLogin().disable();

			httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		}
	}

}
