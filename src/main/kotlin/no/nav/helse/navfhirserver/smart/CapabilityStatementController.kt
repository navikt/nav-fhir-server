package no.nav.helse.navfhirserver.smart

import ca.uhn.fhir.parser.IParser
import org.hl7.fhir.r4.model.CapabilityStatement
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.Enumerations
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class CapabilityStatementController(
  private val fhirJsonParser: IParser
) {

  @GetMapping("/metadata", produces = ["application/json"])
  fun getCapabilities(): String {
    val capabilityStatement = CapabilityStatement().apply {
      status = Enumerations.PublicationStatus.ACTIVE
      date = Date()
      publisher = "NAV IT"
      kind = CapabilityStatement.CapabilityStatementKind.INSTANCE
      format = listOf(CodeType("json"), CodeType("xml"))
      fhirVersion = Enumerations.FHIRVersion._4_0_1
      addRest().apply {
        mode = CapabilityStatement.RestfulCapabilityMode.SERVER
        addResource().apply {
          type = "Patient"
          addInteraction().code = CapabilityStatement.TypeRestfulInteraction.READ
        }
      }
    }

    return fhirJsonParser.encodeResourceToString(capabilityStatement)
  }

}
