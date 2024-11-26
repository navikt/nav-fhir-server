package no.nav.helse.navfhirserver.smart

import ca.uhn.fhir.parser.IParser
import org.hl7.fhir.r4.model.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class CapabilityStatementController(
  private val fhirJsonParser: IParser,
  @Value("\${server.baseUri}") private val baseUri: String
) {

  @GetMapping("/metadata", produces = ["application/json"])
  fun getCapabilities(): String {
    val capabilityStatement = CapabilityStatement().apply {
      // Server metadata
      status = Enumerations.PublicationStatus.ACTIVE
      date = Date()
      publisher = "NAV IT"
      kind = CapabilityStatement.CapabilityStatementKind.INSTANCE
      software = CapabilityStatement.CapabilityStatementSoftwareComponent().apply {
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
        mode = CapabilityStatement.RestfulCapabilityMode.SERVER

        // Security configuration
        security = CapabilityStatement.CapabilityStatementRestSecurityComponent().apply {
          cors = true
          addExtension().apply {
            url = "http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris"
            extension = listOf(
              addExtension().apply {
                url = "authorize"
                value = UriType("$baseUri/oauth2/fhir/authorize")
              },
              addExtension().apply {
                url = "token"
                value = UriType("$baseUri/oauth2/fhir/token")
              },
              addExtension().apply {
                url = "introspect"
                value = UriType("$baseUri/oauth2/fhir/introspect")
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
            code = CapabilityStatement.TypeRestfulInteraction.READ
          }
          addInteraction().apply {
            code = CapabilityStatement.TypeRestfulInteraction.SEARCHTYPE
          }
          addInteraction().apply {
            code = CapabilityStatement.TypeRestfulInteraction.CREATE
          }
          addInteraction().apply {
            code = CapabilityStatement.TypeRestfulInteraction.UPDATE
          }
          addInteraction().apply {
            code = CapabilityStatement.TypeRestfulInteraction.DELETE
          }
        }

        addResource().apply {
          type = "Practitioner"
          profile = "http://hl7.no/fhir/StructureDefinition/no-basis-Practitioner"

          addInteraction().apply {
            code = CapabilityStatement.TypeRestfulInteraction.READ
          }
          addInteraction().apply {
            code = CapabilityStatement.TypeRestfulInteraction.SEARCHTYPE
          }
        }

        addResource().apply {
          type = "Encounter"
          profile = "http://hl7.no/fhir/StructureDefinition/no-basis-Encounter"

          addInteraction().apply {
            code = CapabilityStatement.TypeRestfulInteraction.READ
          }
          addInteraction().apply {
            code = CapabilityStatement.TypeRestfulInteraction.SEARCHTYPE
          }
          addInteraction().apply {
            code = CapabilityStatement.TypeRestfulInteraction.CREATE
          }
          addInteraction().apply {
            code = CapabilityStatement.TypeRestfulInteraction.UPDATE
          }
          addInteraction().apply {
            code = CapabilityStatement.TypeRestfulInteraction.DELETE
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
