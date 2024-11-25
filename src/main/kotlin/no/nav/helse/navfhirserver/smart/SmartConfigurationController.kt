package no.nav.helse.navfhirserver.smart

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SmartConfigurationController(@Value("\${server.baseUri}") private val baseUri: String) {

    @GetMapping("/.well-known/smart-configuration", produces = ["application/json"])
    fun getSmartConfiguration(): ResponseEntity<SmartConfiguration> {
        val config =
            SmartConfiguration(
                issuer = baseUri,
                jwksUri = "$baseUri/oauth2/fhir/jwks",
                authorizationEndpoint = "$baseUri/oauth2/fhir/authorize",
                tokenEndpoint = "$baseUri/oauth2/fhir/token",
                managementEndpoint = "$baseUri/oauth2/fhir/manage",
                introspectionEndpoint = "$baseUri/oauth2/fhir/introspect",
                revocationEndpoint = "$baseUri/oauth2/fhir/revoke",
                userAccessBrandBundle = "$baseUri/oauth2/fhir/bundle",
                userAccessBrandIdentifier = "$baseUri/oauth2/fhir/identifier",
                grantTypesSupported =
                    listOf("authorization_code", "client_credentials", "refresh_token"),
                scopesSupported =
                    listOf(
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
                        "offline_access",
                    ),
                responseTypesSupported = listOf("code", "token"),
                capabilities =
                    listOf(
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
                        "authorize-post",
                    ),
                codeChallengeMethodsSupported = listOf("256"),
            )

        return ResponseEntity.ok(config)
    }
}
