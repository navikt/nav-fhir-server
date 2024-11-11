package no.nav.helse.navfhirserver.smart

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/.well-known")
class SmartConfigurationController(
    private val smartConfig: SmartConfigurationProperties
) {

    @GetMapping("/smart-configuration", produces = ["application/json"])
    fun getSmartConfiguration(): Map<String, Any> {
        return mapOf(
            "issuer" to smartConfig.issuer,
            "jwks_uri" to smartConfig.jwksUri,
            "authorization_endpoint" to smartConfig.authorizationEndpoint,
            "token_endpoint" to smartConfig.tokenEndpoint,
            "management_endpoint" to smartConfig.managementEndpoint,
            "introspection_endpoint" to smartConfig.introspectionEndpoint,
            "revocation_endpoint" to smartConfig.revocationEndpoint,
            "user_access_brand_bundle" to smartConfig.userAccessBrandBundle,
            "user_access_brand_identifier" to smartConfig.userAccessBrandIdentifier,
            "grant_types_supported" to smartConfig.grantTypesSupported,
            "scopes_supported" to smartConfig.scopesSupported,
            "response_types_supported" to smartConfig.responseTypesSupported,
            "capabilities" to smartConfig.capabilities,
            "code_challenge_methods_supported" to smartConfig.codeChallengeMethodsSupported
        )
    }

}