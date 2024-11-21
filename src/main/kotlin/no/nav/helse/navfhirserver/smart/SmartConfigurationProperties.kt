package no.nav.helse.navfhirserver.smart

data class SmartConfiguration(
    val issuer: String,
    val jwksUri: String,
    val authorizationEndpoint: String,
    val tokenEndpoint: String,
    val managementEndpoint: String,
    val introspectionEndpoint: String,
    val revocationEndpoint: String,
    val userAccessBrandBundle: String,
    val userAccessBrandIdentifier: String,
    val grantTypesSupported: List<String>,
    val scopesSupported: List<String>,
    val responseTypesSupported: List<String>,
    val capabilities: List<String>,
    val codeChallengeMethodsSupported: List<String>
)