package no.nav.helse.navfhirserver.data

import ca.uhn.fhir.parser.IParser
import org.springframework.stereotype.Service

@Service
class FhirDataService(
    private val fhirDatasource: FhirDataSource,
    private val fhirJsonParser: IParser
) {

  fun getExamplePatient(uuid: String): String {
    val examplePatient = fhirDatasource.getPatient(uuid)
    return fhirJsonParser.encodeResourceToString(examplePatient)
  }

  fun getExamplePractitioner(uuid: String): String {
    val examplePractitioner = fhirDatasource.getPractitioner(uuid)
    return fhirJsonParser.encodeResourceToString(examplePractitioner)
  }

  fun getExampleEncounter(uuid: String): String {
    val exampleEncounter = fhirDatasource.getEncounter(uuid)
    return fhirJsonParser.encodeResourceToString(exampleEncounter)
  }
}
