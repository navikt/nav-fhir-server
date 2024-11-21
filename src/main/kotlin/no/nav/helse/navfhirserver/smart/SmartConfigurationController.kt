package no.nav.helse.navfhirserver.smart

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SmartConfigurationController(@Value("\${server.base-url}") private val baseUrl: String) {



    @GetMapping("/.well-known/smart-configuration", produces = ["application/json"])
    fun getSmartConfiguration(): ResponseEntity<SmartConfiguration> {
        val config = SmartConfiguration(
            issuer = baseUrl,
            jwksUri = "$baseUrl/v1/oauth2/jwks",
            authorizationEndpoint = "$baseUrl/v1/oauth2/authorize",
            tokenEndpoint = "$baseUrl/v1/oauth2/token",
            managementEndpoint = "$baseUrl/v1/oauth2/manage",
            introspectionEndpoint = "$baseUrl/v1/oauth2/introspect",
            revocationEndpoint = "$baseUrl/v1/oauth2/revoke",
            userAccessBrandBundle = "$baseUrl/v1/user/bundle",
            userAccessBrandIdentifier = "$baseUrl/v1/user/bundle/identifier",
            grantTypesSupported = listOf("authorization_code"),
            scopesSupported = listOf(
                "openid",
                "profile",
                "fhirUser",
                "launch",
                "patient/*.cruds",
                "patient/*.*",
                "encounter/*.cruds",
                "encounter/*.*",
                "user/*.cruds",
                "user/*.*",
                "offline_access"
            ),
            responseTypesSupported = listOf("code", "token"),
            capabilities = listOf(
                "launch-ehr",
                "launch-standalone",
                "client-public",
                "client-confidential-symmetric",
                "client-confidential-asymmetric",
                "sso-openid-connect",
                "context-passthrough-banner",
                "context-passthrough-style",
                "context-ehr-patient",
                "context-ehr-encounter",
                "context-standalone-patient",
                "context-standalone-encounter",
                "permission-offline",
                "permission-patient",
                "permission-user",
                "permission-v1",
                "permission-v2",
                "authorize-post"
            ),
            codeChallengeMethodsSupported = listOf("256")
        )

        return ResponseEntity.ok(config)
    }

}