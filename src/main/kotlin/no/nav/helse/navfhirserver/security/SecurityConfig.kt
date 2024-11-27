package no.nav.helse.navfhirserver.security

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity(debug = true)
class SecurityConfig(
    @Value("\${server.baseUri}") private val baseUri: String,
    @Value("\${client.redirect-uri}") private val clientRedirectUri: String,
) {

    @Bean
    @Order(1)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val authConfig = OAuth2AuthorizationServerConfigurer.authorizationServer()
        http.securityMatcher(authConfig.endpointsMatcher).with(authConfig) { authorizationServer ->
            authorizationServer.oidc(Customizer.withDefaults())
        }
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

                // Not accessible on public ingress anyway
                authorize("/internal/**", permitAll)

                // Fake EPJ (should be secured by wonderwall)
                authorize("/", permitAll) // TODO secure
                authorize("/epj/**", permitAll) // TODO secure

                // General
                authorize("/.well-known/**", permitAll)
                authorize("/metadata", permitAll)
                authorize("/error", permitAll)
                authorize(anyRequest, authenticated)
            }
            cors { configurationSource = corsConfigurationSource() }
            csrf { disable() }
        }
        return http.build()
    }

    @Bean
    fun devClientRepository(): RegisteredClientRepository {
        val asymmetricClient =
            RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("syk-inn")
                .clientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("fhirUser")
                .scope("launch")
                .scope("patient/*.*")
                .scope("user/*.*")
                .scope("offline_access")
                .redirectUri(clientRedirectUri)
                .clientSettings(ClientSettings.builder().requireProofKey(true).build())
                .build()
        return InMemoryRegisteredClientRepository(asymmetricClient)
    }

    @Bean
    fun jwkSource(): ImmutableJWKSet<*> {
        val keyPair = generateRsaKey()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey

        val rsaKey =
            RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build()

        return ImmutableJWKSet<SecurityContext>(JWKSet(rsaKey))
    }

    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings {
        return AuthorizationServerSettings.builder()
            .issuer(baseUri)
            .authorizationEndpoint("/fhir/r4/oauth2/authorize")
            .tokenEndpoint("/fhir/r4/oauth2/token")
            .tokenIntrospectionEndpoint("/fhir/r4/oauth2/introspect")
            .tokenRevocationEndpoint("/fhir/r4/oauth2/revoke")
            .jwkSetEndpoint("/.well-known/jwks")
            .oidcLogoutEndpoint("/fhir/r4/user/logout")
            .oidcUserInfoEndpoint("/fhir/r4/user/userinfo")
            .oidcClientRegistrationEndpoint("/fhir/r4/client/register")
            .build()
    }

    private fun generateRsaKey(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.generateKeyPair()
    }

    fun corsConfigurationSource(): CorsConfigurationSource {
        val config =
            CorsConfiguration().apply {
                allowedOriginPatterns =
                    listOf("https://*.dev.nav.no", "http://localhost:[*]", "http://127.0.0.1:[*]")
                allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                allowedHeaders = listOf("Authorization", "Content-Type", "X-Requested-With")
                allowCredentials = true
                maxAge = 3600L
            }

        return UrlBasedCorsConfigurationSource().apply { registerCorsConfiguration("/**", config) }
    }
}
