package su.reddot.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import su.reddot.infrastructure.security.RestAuthenticationFilter;
import su.reddot.infrastructure.security.provider.EmailRestAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Order(1)
public class RestSecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final EmailRestAuthenticationProvider restAuthenticationProvider;

	@Autowired
	public RestSecurityConfiguration(EmailRestAuthenticationProvider restAuthenticationProvider) {
		this.restAuthenticationProvider = restAuthenticationProvider;
	}

	@Autowired
	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(this.restAuthenticationProvider);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/mobile/api/v1/users/registration");
		web.ignoring().antMatchers("/mobile/api/v1/catalog/new2");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.antMatcher("/mobile/api/**")
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.addFilterBefore(new RestAuthenticationFilter(this.authenticationManager()), AnonymousAuthenticationFilter.class);
		http.antMatcher("/mobile/api/**")
				.csrf()
				.disable();
	}
}
