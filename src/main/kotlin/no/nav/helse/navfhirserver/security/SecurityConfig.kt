package no.nav.helse.navfhirserver.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity(debug = true)
class SecurityConfig() {

    @Bean
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

                // General
                authorize("/.well-known/**", permitAll)
                authorize("/metadata", permitAll)
                authorize("/error/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            cors { configurationSource = corsConfigurationSource() }
            csrf { disable() }
        }
        return http.build()
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
