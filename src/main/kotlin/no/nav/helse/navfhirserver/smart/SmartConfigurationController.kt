package no.nav.helse.navfhirserver.smart

import ca.uhn.fhir.parser.IParser
import org.hl7.fhir.r4.model.*
import org.hl7.fhir.r4.model.CapabilityStatement.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class SmartConfigurationController(
    private val fhirJsonParser: IParser,
    private val smartConfig: SmartConfigurationProperties
) {

    @GetMapping("/.well-known/smart-configuration", produces = ["application/json"])
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

    @GetMapping("/metadata", produces = ["application/json"])
    fun getCapabilities(): String {
        val capabilityStatement = CapabilityStatement().apply {
            // Server metadata
            status = Enumerations.PublicationStatus.ACTIVE
            date = Date()
            publisher = "NAV IT"
            kind = CapabilityStatementKind.INSTANCE
            software = CapabilityStatementSoftwareComponent().apply {
                name = "NAV FHIR server"
                version = "1"
            }

            // FHIR version and supported formats
            fhirVersion = Enumerations.FHIRVersion._4_0_1
            format = listOf(
                CodeType("application/fhir+json"),
                CodeType("application/json")
            )

            // REST configuration
            addRest().apply {
                mode = RestfulCapabilityMode.SERVER

                // Security configuration
                security = CapabilityStatementRestSecurityComponent().apply {
                    cors = true
                    addExtension().apply {
                        url = "http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris"
                        extension = listOf(
                            addExtension().apply {
                                url = "authorize"
                                setValue(UriType(smartConfig.authorizationEndpoint))
                            },
                            addExtension().apply {
                                url = "token"
                                setValue(UriType(smartConfig.tokenEndpoint))
                            },
                            addExtension().apply {
                                url = "introspect"
                                setValue(UriType(smartConfig.introspectionEndpoint))
                            }
                        )
                    }
                    service.add(
                        CodeableConcept().apply {
                            addCoding().apply {
                                system = "http://hl7.org/fhir/restful-security-service"
                                code = "SMART-on-FHIR"
                                display = "SMART-on-FHIR"
                            }
                        }
                    )
                    description = "Authentication via OAuth2 using SMART on FHIR framework (see http://docs.smarthealthit.org)"
                }

                // Supported resources
                addResource().apply {
                    type = "Patient"
                    profile = "http://hl7.no/fhir/StructureDefinition/no-basis-Patient"

                    // Supported interactions
                    addInteraction().apply {
                        code = TypeRestfulInteraction.READ
                    }
                    addInteraction().apply {
                        code = TypeRestfulInteraction.SEARCHTYPE
                    }
                    addInteraction().apply {
                        code = TypeRestfulInteraction.CREATE
                    }
                    addInteraction().apply {
                        code = TypeRestfulInteraction.UPDATE
                    }
                    addInteraction().apply {
                        code = TypeRestfulInteraction.DELETE
                    }
                }

                addResource().apply {
                    type = "Practitioner"
                    profile = "http://hl7.no/fhir/StructureDefinition/no-basis-Practitioner"

                    addInteraction().apply {
                        code = TypeRestfulInteraction.READ
                    }
                    addInteraction().apply {
                        code = TypeRestfulInteraction.SEARCHTYPE
                    }
                }

                addResource().apply {
                    type = "Encounter"
                    profile = "http://hl7.no/fhir/StructureDefinition/no-basis-Encounter"

                    addInteraction().apply {
                        code = TypeRestfulInteraction.READ
                    }
                    addInteraction().apply {
                        code = TypeRestfulInteraction.SEARCHTYPE
                    }
                    addInteraction().apply {
                        code = TypeRestfulInteraction.CREATE
                    }
                    addInteraction().apply {
                        code = TypeRestfulInteraction.UPDATE
                    }
                    addInteraction().apply {
                        code = TypeRestfulInteraction.DELETE
                    }
                }

                addOperation().apply {
                    name = "metadata"
                    definition = "CapabilityStatement"
                }
            }
        }

        return fhirJsonParser.encodeResourceToString(capabilityStatement)
    }
}
