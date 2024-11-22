package no.nav.helse.navfhirserver.smart

import org.springframework.stereotype.Component

@Component
class SmartScopeUtil {

    private val actionMappings =
        mapOf(
            "c" to "CREATE",
            "r" to "READ",
            "u" to "UPDATE",
            "d" to "DELETE",
            "s" to "SEARCH",
            "*" to "FULL",
        )

    fun normalizeScopes(requestedScopes: Set<String>): Set<String> {
        val smartScopes = Regex("(patient|user|system)/[^.]+\\.(?:\\*|[cruds]+)\$")

        return requestedScopes
            .flatMap { scope ->
                when {
                    scope in setOf("openid", "profile", "launch", "fhirUser", "offline_access") ->
                        setOf(scope)

                    scope.matches(smartScopes) -> normalizeResourceScope(scope)
                    else -> emptySet()
                }
            }
            .toSet()
    }

    fun normalizeResourceScope(scope: String): Set<String> {
        val (context, resourceAction) = scope.split("/")
        val (resource, actions) = resourceAction.split(".")

        return actions
            .map { action -> action.toString() }
            .map { "$context/$resource.${actionMappings[it]}" }
            .toSet()
    }
}
