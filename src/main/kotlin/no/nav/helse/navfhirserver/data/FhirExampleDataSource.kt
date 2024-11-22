package no.nav.helse.navfhirserver.data

import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Repository
import java.text.SimpleDateFormat
import java.util.*

@Repository
class FhirExampleDataSource : FhirDataSource {

    override fun getPatient(uuid: String): Patient {
        val patient = Patient()

        patient.meta.addProfile("http://hl7.no/fhir/StructureDefinition/no-basis-Patient")

        val citizenshipExtension = patient.addExtension()
        citizenshipExtension.url = "http://hl7.org/fhir/StructureDefinition/patient-citizenship"

        val citizenshipCodeExtension = citizenshipExtension.addExtension()
        citizenshipCodeExtension.url = "code"

        val codeableConcept = CodeableConcept()
        val coding = codeableConcept.addCoding()
        coding.system = "urn:iso:std:iso:3166"
        coding.code = "NO"
        citizenshipCodeExtension.setValue(codeableConcept)

        patient.addIdentifier().setSystem("urn:oid:2.16.578.1.12.4.1.4.1").setValue("21030550231")
        patient.addIdentifier().setSystem("http://profil.visma.no/lopenummer").setValue("123")

        patient.addName().setFamily("Søreng").addGiven("Jannice")

        patient
            .addTelecom()
            .setSystem(ContactPoint.ContactPointSystem.PHONE)
            .setValue("+4733445566")
            .setUse(ContactPoint.ContactPointUse.HOME)

        patient.gender = Enumerations.AdministrativeGender.FEMALE

        val address = patient.addAddress()
        address.use = Address.AddressUse.HOME
        address.addLine("Karisvingen 44")
        address.city = "Oslo"
        address.district = "Oslo"
        address.postalCode = "0603"
        address.country = "NO"

        val propertyInfoExtension = address.addExtension()
        propertyInfoExtension.url =
            "http://hl7.no/fhir/StructureDefinition/no-basis-propertyinformation"

        val municipalityExtension = propertyInfoExtension.addExtension()
        municipalityExtension.url = "municipality"

        val municipalityCoding = Coding()
        municipalityCoding.system = "urn:oid:2.16.578.1.12.4.1.1.3402"
        municipalityCoding.code = "0301"
        municipalityCoding.display = "Oslo"
        municipalityExtension.setValue(municipalityCoding)

        val officialAddressExtension = address.addExtension()
        officialAddressExtension.url =
            "http://hl7.no/fhir/StructureDefinition/no-basis-address-official"
        officialAddressExtension.setValue(BooleanType(true))

        val districtExtension = address.addExtension()
        districtExtension.url = "http://hl7.no/fhir/StructureDefinition/no-basis-municipalitycode"

        val districtCoding = Coding()
        districtCoding.system =
            "https://register.geonorge.no/subregister/sosi-kodelister/kartverket/kommunenummer-alle"
        districtCoding.code = "0301"
        districtExtension.setValue(districtCoding)

        val maritalStatus = CodeableConcept()
        val maritalStatusCoding = maritalStatus.addCoding()
        maritalStatusCoding.system = "http://terminology.hl7.org/CodeSystem/v3-MaritalStatus"
        maritalStatusCoding.code = "U"
        maritalStatusCoding.display = "unmarried"
        patient.maritalStatus = maritalStatus

        val contact = patient.addContact()

        val relationshipCoding = contact.addRelationship()
        relationshipCoding
            .addCoding()
            .setSystem("http://terminology.hl7.org/CodeSystem/v2-0131")
            .setCode("C")
            .setDisplay("Emergency Contact")

        contact.addRelationship().setText("Bror")

        val contactName = contact.name
        contactName.text = "Pål Pårørende Olsen"
        contactName.family = "Olsen"
        contactName.addGiven("Pål")

        val middleNameExtension = contactName.addExtension()
        middleNameExtension.url = "http://hl7.no/fhir/StructureDefinition/no-basis-middlename"
        middleNameExtension.setValue(StringType("Pårørende"))

        patient.addCommunication().setLanguage(CodeableConcept().setText("Norsk"))

        val gp = patient.addGeneralPractitioner()
        gp.identifier = Identifier().setSystem("urn:oid:2.16.578.1.12.4.1.2").setValue("720")
        gp.display = "SIDSEL AASE JAVERY"

        return patient
    }

    override fun getPractitioner(uuid: String): Practitioner {
        val practitioner = Practitioner()

        practitioner.meta.addProfile("http://hl7.no/fhir/StructureDefinition/no-basis-Practitioner")
        practitioner.meta.addProfile(
            "http://ehelse.no/fhir/StructureDefinition/no-helseapi-Practitioner"
        )

        practitioner.active = true

        practitioner
            .addIdentifier()
            .setSystem("urn:oid:2.16.578.1.12.4.1.4.4")
            .setValue("9144889")
            .setUse(Identifier.IdentifierUse.OFFICIAL)
            .addExtension(
                "http://hl7.no/fhir/StructureDefinition/no-basis-hpr-number",
                BooleanType(true),
            )

        practitioner
            .addIdentifier()
            .setSystem("urn:oid:2.16.578.1.12.4.1.4.1")
            .setValue("13116900216")
            .setUse(Identifier.IdentifierUse.OFFICIAL)

        val name = practitioner.addName()
        name.use = HumanName.NameUse.OFFICIAL
        name.setFamily("Koman")
        name.addGiven("Magnar")

        val middleNameExtension = name.addExtension()
        middleNameExtension.url = "http://hl7.no/fhir/StructureDefinition/no-basis-middlename"
        middleNameExtension.setValue(StringType("Magnar"))

        practitioner
            .addTelecom()
            .setSystem(ContactPoint.ContactPointSystem.PHONE)
            .setValue("75589889")
            .setUse(ContactPoint.ContactPointUse.WORK)
            .setPeriod(Period().setStart(Date()))

        practitioner
            .addTelecom()
            .setSystem(ContactPoint.ContactPointSystem.FAX)
            .setValue("75589889")

        practitioner
            .addTelecom()
            .setSystem(ContactPoint.ContactPointSystem.EMAIL)
            .setValue("mako@bottomline.no")
            .setUse(ContactPoint.ContactPointUse.WORK)

        practitioner.gender = Enumerations.AdministrativeGender.MALE

        practitioner.birthDate = SimpleDateFormat("yyyy-MM-dd").parse("1969-11-13")

        val address = practitioner.addAddress()
        address.use = Address.AddressUse.WORK
        address.type = Address.AddressType.PHYSICAL
        address.addLine("Dreyfushammarn 23")
        address.city = "BODØ"
        address.postalCode = "8012"
        address.country = "NO"

        val propertyInfoExtension = address.addExtension()
        propertyInfoExtension.url =
            "http://hl7.no/fhir/StructureDefinition/no-basis-propertyinformation"

        val municipalityExtension = propertyInfoExtension.addExtension()
        municipalityExtension.url = "municipality"
        val municipalityCoding = Coding()
        municipalityCoding.system = "urn:oid:2.16.578.1.12.4.1.1.3402"
        municipalityCoding.code = "1804"
        municipalityCoding.display = "Bodø"
        municipalityExtension.setValue(municipalityCoding)

        val officialAddressExtension = address.addExtension()
        officialAddressExtension.url =
            "http://hl7.no/fhir/StructureDefinition/no-basis-address-official"
        officialAddressExtension.setValue(BooleanType(true))

        val qualification1 = practitioner.addQualification()
        val code1 = qualification1.code
        val coding1 = code1.addCoding()
        coding1.system = "urn:oid:2.16.578.1.12.4.1.1.9060"
        coding1.code = "LE"
        coding1.display = "Lege"

        val authExtension = qualification1.addExtension()
        authExtension.url =
            "http://ehelse.no/fhir/StructureDefinition/no-basis-authorization-status"
        authExtension.setValue(CodeType("active"))

        val qualification2 = practitioner.addQualification()
        val code2 = qualification2.code
        val coding2 = code2.addCoding()
        coding2.system = "urn:oid:2.16.578.1.12.4.1.1.7426"
        coding2.code = "1"
        coding2.display = "Allmennmedisin"

        val qualification3 = practitioner.addQualification()
        val code3 = qualification3.code
        val coding3 = code3.addCoding()
        coding3.system = "urn:oid:2.16.578.1.12.4.1.1.7704"
        coding3.code = "1"
        coding3.display = "Autorisasjon"

        practitioner
            .addCommunication()
            .setText("Norsk")
            .addCoding()
            .setSystem("urn:ietf:bcp:47")
            .setCode("nb")
            .setDisplay("Norsk bokmål")

        practitioner
            .addCommunication()
            .setText("English")
            .addCoding()
            .setSystem("urn:ietf:bcp:47")
            .setCode("en")
            .setDisplay("English")

        return practitioner
    }

    override fun getEncounter(uuid: String): Encounter {
        val encounter = Encounter()

        encounter.meta.addProfile("http://hl7.no/fhir/StructureDefinition/no-basis-Encounter")
        encounter.meta.addProfile("http://ehelse.no/fhir/StructureDefinition/no-helseapi-Encounter")

        encounter.status = Encounter.EncounterStatus.FINISHED

        encounter.class_ =
            Coding().apply {
                system = "http://ehelse.no/fhir/CodeSystem/no-basis-emergency-service-type"
                code = "LV"
                display = "Legevakt"
            }

        encounter.addType().apply {
            addCoding().apply {
                system = "urn:oid:2.16.578.1.12.4.1.1.9277"
                code = "2"
                display = "Konsultasjon"
            }
        }

        encounter.subject =
            Reference().apply {
                reference = "Patient/21030550231"
                display = "Jannice Søreng"
            }

        encounter.addParticipant().apply {
            addType().addCoding().apply {
                system = "urn:oid:2.16.578.1.12.4.1.1.9034"
                code = "LE"
                display = "Lege"
            }
            individual =
                Reference().apply {
                    reference = "Practitioner/9144889"
                    display = "Magnar Koman"
                }
        }

        encounter.addParticipant().apply {
            addType().addCoding().apply {
                system = "urn:oid:2.16.578.1.12.4.1.1.9034"
                code = "SP"
                display = "Sykepleier"
            }
            individual =
                Reference().apply {
                    reference = "Practitioner/98765"
                    display = "Anna Hansen"
                }
        }

        val norwegianTimeZone = TimeZone.getTimeZone("Europe/Oslo")
        val calendar = Calendar.getInstance(norwegianTimeZone)
        encounter.period =
            Period().apply {
                start = calendar.time
                calendar.add(Calendar.HOUR_OF_DAY, 1)
                end = calendar.time
            }

        encounter.serviceProvider =
            Reference().apply {
                reference = "Organization/12345"
                display = "Bodø Legesenter"
            }

        encounter.addReasonCode().apply {
            addCoding().apply {
                system = "urn:oid:2.16.578.1.12.4.1.1.7170"
                code = "A01"
                display = "Smerte generell/flere steder"
            }
        }

        encounter.priority =
            CodeableConcept().apply {
                addCoding().apply {
                    system = "urn:oid:2.16.578.1.12.4.1.1.9278"
                    code = "G"
                    display = "Grønn (vanlig)"
                }
            }

        encounter.addLocation().apply {
            location =
                Reference().apply {
                    reference = "Location/12345"
                    display = "Konsultasjonsrom 1"
                }
            status = Encounter.EncounterLocationStatus.COMPLETED
            addExtension().apply {
                url = "http://hl7.no/fhir/StructureDefinition/no-basis-municipalitycode"
                setValue(
                    Coding().apply {
                        system = "urn:oid:2.16.578.1.12.4.1.1.3402"
                        code = "1804"
                        display = "Bodø"
                    }
                )
            }
        }

        encounter.addExtension().apply {
            url = "http://ehelse.no/fhir/StructureDefinition/no-basis-encounter-cause"
            setValue(
                CodeableConcept().apply {
                    addCoding().apply {
                        system = "http://ehelse.no/fhir/CodeSystem/no-basis-encounter-cause"
                        code = "1"
                        display = "Sykdom"
                    }
                }
            )
        }

        encounter.addExtension().apply {
            url = "http://ehelse.no/fhir/StructureDefinition/no-basis-rule-patient-contribution"
            setValue(
                CodeableConcept().apply {
                    addCoding().apply {
                        system = "urn:oid:2.16.578.1.12.4.1.1.9125"
                        code = "1"
                        display = "Betaler egenandel"
                    }
                }
            )
        }

        encounter.addDiagnosis().apply {
            condition = Reference("Condition/12345")
            use =
                CodeableConcept().apply {
                    addCoding().apply {
                        system = "http://terminology.hl7.org/CodeSystem/diagnosis-role"
                        code = "DD"
                        display = "Discharge diagnosis"
                    }
                }
            rank = 1
            addExtension().apply {
                url = "http://ehelse.no/fhir/StructureDefinition/no-basis-diagnosis-type"
                setValue(
                    CodeableConcept().apply {
                        addCoding().apply {
                            system = "urn:oid:2.16.578.1.12.4.1.1.7110"
                            code = "HD"
                            display = "Hoveddiagnose"
                        }
                    }
                )
            }
        }

        encounter.addExtension().apply {
            url = "http://ehelse.no/fhir/StructureDefinition/no-basis-service-type"
            setValue(
                CodeableConcept().apply {
                    addCoding().apply {
                        system = "urn:oid:2.16.578.1.12.4.1.1.9040"
                        code = "10"
                        display = "Allmennlegetjeneste"
                    }
                }
            )
        }

        encounter.addExtension().apply {
            url = "http://ehelse.no/fhir/StructureDefinition/no-basis-administrative-contact-type"
            setValue(
                CodeableConcept().apply {
                    addCoding().apply {
                        system = "urn:oid:2.16.578.1.12.4.1.1.9051"
                        code = "1"
                        display = "Fysisk oppmøte"
                    }
                }
            )
        }

        return encounter
    }
}
