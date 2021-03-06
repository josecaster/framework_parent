package software.simple.solutions.framework.core.config.oauth;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	private final AuthenticationManager authenticationManager;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private SimpleUserDetailsService simpleUserDetailsService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	AuthorizationServerConfig(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
		configurer.jdbc(jdbcTemplate.getDataSource());
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(this.tokenStore()).accessTokenConverter(jwtAccessTokenConverter())
				.userDetailsService(simpleUserDetailsService).authenticationManager(this.authenticationManager);
	}

	@Bean
	JwtAccessTokenConverter jwtAccessTokenConverter() {
		KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource(".keystore-oauth2-demo"),
				"admin1234".toCharArray());
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
		jwtAccessTokenConverter.setKeyPair(factory.getKeyPair("oauth2-demo-key"));
		return jwtAccessTokenConverter;
	}

	@Bean
	TokenStore tokenStore() {
		// return new JwtTokenStore(this.jwtAccessTokenConverter());
		return new JdbcTokenStore(dataSource);
	}
}
