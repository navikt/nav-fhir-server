package no.nav.helse.navfhirserver.data

import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Practitioner

interface FhirDataSource {
    fun getPatient(uuid: String): Patient

    fun getPractitioner(uuid: String): Practitioner

    fun getEncounter(uuid: String): Encounter
}
