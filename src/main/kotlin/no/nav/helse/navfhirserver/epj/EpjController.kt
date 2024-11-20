package no.nav.helse.navfhirserver.epj

import org.springframework.ui.Model
import org.springframework.stereotype.Controller
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

// TODO: From some kinf of service, inject it with BEANS 🫘 🫘 🫘 🫘 🫘 🫘
private data class Patient(val name: String, val fnr: String)

private val patients = listOf(
    Patient("Ola Nordmann", "45847100951"),
    Patient("Kari Karisdottir", "65927600603"),
)

@Controller
@RequestMapping("/epj")
class EpjController {

    private data class User(
        val name: String,
        val hpr: String,
    )

    private data class App(val name: String, val clientId: String, val url: String)

    private val apps = listOf(
        App(
            name = "Sykmeldinger",
            clientId = "syk-inn",
            url = "https://www.ekstern.dev.nav.no/samarbeidspartner/sykmelding/fhir"
        ),
        App(
            name = "Leo's SMART Probe",
            clientId = "NAV_SMART_on_FHIR_example",
            url = "https://nav-on-fhir.ekstern.dev.nav.no"
        ),
    )

    @GetMapping
    fun index(model: Model): String {
        model["user"] = User("Dr. House O. Apartment", "92391831")

        return "epj"
    }

    @GetMapping("/partials/patient-picker")
    fun patientPicker(model: Model): String {
        model["patients"] = patients

        return "partials/patient-picker"
    }

    @GetMapping("/partials/smart-picker")
    fun appsPicker(model: Model): String {
        model["apps"] = apps

        return "partials/smart-picker"
    }

    @GetMapping("/partials/launch")
    fun launchApp(
        @RequestParam app: String,
        model: Model
    ): String {
        if (app == "none") {
            return "fragments/inner-default"
        }

        val appToLaunch = apps.find { it.clientId == app } ?: throw IllegalArgumentException("Unknown app: $app")
        // TODO: Don't hardcode iss and code
        // val launchUrl = "${appToLaunch.url}/launch?iss=https://www.fhir.dev.nav.no&code=foo-bar-baz"
        val launchUrl = "${appToLaunch.url}/launch"

        model["url"] = launchUrl
        model["name"] = appToLaunch.name

        return "partials/inner-launch"
    }
}

@Controller
@RequestMapping("/epj/consultation")
class ConsultationController {

    @PostMapping("/start")
    fun startConsultation(
        @RequestParam fnr: String,
        model: Model
    ): String {
        model["fnr"] = fnr
        model["name"] = patients.find { it.fnr == fnr }?.name ?: throw IllegalArgumentException("Unknown patient: $fnr")

        // TODO set server context about the consultation
        return "partials/consultation/start"
    }

    @PostMapping("/end")
    fun endConsultation(
        model: Model
    ): String {
        // TODO clear server context about the consultation
        return "partials/consultation/end"
    }
}
