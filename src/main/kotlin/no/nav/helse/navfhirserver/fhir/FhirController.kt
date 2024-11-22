package no.nav.helse.navfhirserver.fhir

import no.nav.helse.navfhirserver.data.FhirDataService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/fhir")
class FhirController(private val fhirDataService: FhirDataService) {

    @GetMapping("/Patient/{id}", produces = ["application/fhir+json"])
    fun getTestPatient(@PathVariable id: String): String {
        return fhirDataService.getExamplePatient(id)
    }

    @GetMapping("/Practitioner/{id}", produces = ["application/fhir+json"])
    fun getTestPractitioner(@PathVariable id: String): String {
        return fhirDataService.getExamplePractitioner(id)
    }

    @GetMapping("/Encounter/{id}", produces = ["application/fhir+json"])
    fun getEncounter(@PathVariable id: String): String {
        return fhirDataService.getExampleEncounter(id)
    }
}
