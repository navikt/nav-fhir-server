package no.nav.helse.navfhirserver.smart

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationProperties(prefix = "smart.configuration")
@ConfigurationPropertiesScan
class SmartConfigurationProperties {
    lateinit var issuer: String
    lateinit var jwksUri: String
    lateinit var authorizationEndpoint: String
    lateinit var tokenEndpoint: String
    lateinit var managementEndpoint: String
    lateinit var introspectionEndpoint: String
    lateinit var revocationEndpoint: String
    lateinit var userAccessBrandBundle: String
    lateinit var userAccessBrandIdentifier: String
    lateinit var grantTypesSupported: List<String>
    lateinit var scopesSupported: List<String>
    lateinit var responseTypesSupported: List<String>
    lateinit var capabilities: List<String>
    lateinit var codeChallengeMethodsSupported: List<String>
}
