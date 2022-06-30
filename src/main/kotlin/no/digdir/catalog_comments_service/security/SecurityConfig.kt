package no.digdir.catalog_comments_service.security

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
open class SecurityConfig {

    @Bean
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf().disable()
            .cors().and()
            .authorizeRequests { authorize ->
                authorize.antMatchers(HttpMethod.OPTIONS).permitAll()
                    .antMatchers(HttpMethod.GET,"/ping").permitAll()
                    .antMatchers(HttpMethod.GET,"/ready").permitAll()
                    .anyRequest().authenticated() }
            .oauth2ResourceServer { resourceServer -> resourceServer.jwt() }
        return http.build()
    }

    @Bean
    open fun jwtDecoder(properties: OAuth2ResourceServerProperties): JwtDecoder {
        val jwtDecoder = NimbusJwtDecoder.withJwkSetUri(properties.jwt.jwkSetUri).build()
        jwtDecoder.setJwtValidator(
            DelegatingOAuth2TokenValidator(
                listOf(
                    JwtTimestampValidator(),
                    JwtIssuerValidator(properties.jwt.issuerUri),
                    JwtClaimValidator("aud") { aud: List<String> -> aud.contains("catalog-comments-service") }
                )
            ))
        return jwtDecoder
    }
}
