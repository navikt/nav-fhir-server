package no.nav.helse.navfhirserver.security

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*

@Configuration
@EnableWebSecurity
class SecurityConfig() {

    @Bean
    @Order(1)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        http
            .getConfigurer(OAuth2AuthorizationServerConfigurer::class.java)
            .oidc(Customizer.withDefaults())
        http
            .exceptionHandling {
                it.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login"))
            }
            .oauth2ResourceServer { it.jwt(Customizer.withDefaults()) }

        return http.build()
    }

    @Bean
    @Order(2)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                // Create
                authorize(
                    HttpMethod.POST,
                    "/User/**",
                    hasAnyAuthority("SCOPE_user/*.c", "SCOPE_user/*.*"),
                )
                authorize(
                    HttpMethod.POST,
                    "/Patient/**",
                    hasAnyAuthority("SCOPE_patient/*.c", "SCOPE_patient/*.*"),
                )
                authorize(
                    HttpMethod.POST,
                    "/Encounter/**",
                    hasAnyAuthority("SCOPE_encounter/*.c", "SCOPE_encounter/*.*"),
                )

                // Read
                authorize(
                    HttpMethod.GET,
                    "/User/**",
                    hasAnyAuthority("SCOPE_user/*.r", "SCOPE_user/*.*"),
                )
                authorize(
                    HttpMethod.GET,
                    "/Patient/**",
                    hasAnyAuthority("SCOPE_patient/*.r", "SCOPE_patient/*.*"),
                )
                authorize(
                    HttpMethod.GET,
                    "/Encounter/**",
                    hasAnyAuthority("SCOPE_encounter/*.r", "SCOPE_encounter/*.*"),
                )

                // Update
                authorize(
                    HttpMethod.PUT,
                    "/User/**",
                    hasAnyAuthority("SCOPE_user/*.u", "SCOPE_user/*.*"),
                )
                authorize(
                    HttpMethod.PUT,
                    "/Patient/**",
                    hasAnyAuthority("SCOPE_patient/*.u", "SCOPE_patient/*.*"),
                )
                authorize(
                    HttpMethod.PUT,
                    "/Encounter/**",
                    hasAnyAuthority("SCOPE_encounter/*.u", "SCOPE_encounter/*.*"),
                )

                // Delete
                authorize(
                    HttpMethod.DELETE,
                    "/User/**",
                    hasAnyAuthority("SCOPE_user/*.d", "SCOPE_user/*.*"),
                )
                authorize(
                    HttpMethod.DELETE,
                    "/Patient/**",
                    hasAnyAuthority("SCOPE_patient/*.d", "SCOPE_patient/*.*"),
                )
                authorize(
                    HttpMethod.DELETE,
                    "/Encounter/**",
                    hasAnyAuthority("SCOPE_encounter/*.d", "SCOPE_encounter/*.*"),
                )

                // General
                authorize("/.well-known/**", permitAll)
                authorize("/metadata", permitAll)
                authorize("/epj", permitAll)
                authorize("/error", permitAll)
                authorize("/v1/**", permitAll)
                authorize("/login", permitAll)
                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer { jwt { jwtDecoder = jwtDecoder() } }
            cors { configurationSource = corsConfigurationSource() }
            csrf { disable() }
        }
        return http.build()
    }

    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        val registeredClient =
            RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("NAV_SMART_on_FHIR_example")
                .clientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .scope("openid")
                .scope("profile")
                .scope("fhirUser")
                .scope("launch")
                .scope("patient/*.*")
                .scope("user/*.*")
                .scope("offline_access")
                .redirectUri("http://localhost:5173")
                .postLogoutRedirectUri("http://localhost:5173")
                .clientSettings(ClientSettings.builder().requireProofKey(true).build())
                .build()
        return InMemoryRegisteredClientRepository(registeredClient)
    }

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings {
        return AuthorizationServerSettings.builder()
            .issuer("http://localhost:9000")
            .build()
    }

    @Bean
    fun jwkSource(): ImmutableJWKSet<*> {
        val keyPair = generateRsaKey()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey

        val rsaKey = RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build()

        return ImmutableJWKSet<SecurityContext>(JWKSet(rsaKey))
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        val user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("password")
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(user);
    }

    fun corsConfigurationSource(): CorsConfigurationSource {
        val config =
            CorsConfiguration().apply {
                allowedOrigins =
                    listOf(
                        "http://localhost:5173",
                        "https://syk-inn.ekstern.dev.nav.no",
                        "https://nav-on-fhir.ekstern.dev.nav.no",
                    )
                allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                allowedHeaders = listOf("Authorization", "Content-Type", "X-Requested-With")
                allowCredentials = true
                maxAge = 3600L
            }

        return UrlBasedCorsConfigurationSource().apply { registerCorsConfiguration("/**", config) }
    }

    private fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withJwkSetUri("http://localhost:9000/v1/oauth2/jwks").build()
    }

    private fun generateRsaKey(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.generateKeyPair()
    }
}
