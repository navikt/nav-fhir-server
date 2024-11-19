package no.nav.helse.navfhirserver.epj

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller
@RequestMapping("/epj")
class EpjController {

    // TODO: From some kinf of service, inject it with BEANS 🫘 🫘 🫘 🫘 🫘 🫘
    private data class Patient(val name: String, val fnr: String)

    private val patients = listOf(
        Patient("Ola Nordmann", "45847100951"),
        Patient("Kari Karisdottir", "65927600603"),
    )

    private data class App(val name: String, val clientId: String, val url: String)

    private val apps = listOf(
        App(
            name = "Sykmeldinger",
            clientId = "syk-inn",
            url = "https://www.ekstern.dev.nav.no/samarbeidspartner/sykmelding/fhir"
        ),
    )

    @GetMapping
    fun index(model: Model): String {
        model["patients"] = patients

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
        val appToLaunch = apps.find { it.clientId == app } ?: throw IllegalArgumentException("Unknown app: $app")
        // TODO: Don't hardcode iss and code
        val launchUrl = "${appToLaunch.url}/launch?iss=https://www.fhir.dev.nav.no&code=foo-bar-baz"

        model["url"] = launchUrl

        return "partials/inner-launch"
    }
}

