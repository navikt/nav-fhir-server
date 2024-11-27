package no.nav.helse.navfhirserver.epj

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

// TODO: From some kinf of service, inject it with BEANS 🫘 🫘 🫘 🫘 🫘 🫘
private data class Patient(val name: String, val fnr: String)

private val patients =
    listOf(Patient("Ola Nordmann", "45847100951"), Patient("Kari Karisdottir", "65927600603"))

@Controller
@RequestMapping("/")
class FakeLoginController {

    @GetMapping(produces = ["text/html"])
    fun index(model: Model): String {
        return "index"
    }
}

@Controller
@RequestMapping("/epj")
class EpjController(val userState: EpjUserState) {

    private data class User(val name: String, val hpr: String)

    private data class App(val name: String, val clientId: String, val url: String)

    private val apps =
        listOf(
            App(
                name = "Sykmeldinger",
                clientId = "syk-inn",
                url = "https://www.ekstern.dev.nav.no/samarbeidspartner/sykmelding/fhir",
            ),
            App(
                name = "Leo's SMART Probe",
                clientId = "NAV_SMART_on_FHIR_example",
                url = "https://nav-on-fhir.ekstern.dev.nav.no",
            ),
            App(name = "Localhost Dev", clientId = "localhost", url = "http://localhost:3000/fhir"),
            App(
                name = "MSIS-melding",
                clientId = "msis",
                url = "https://test-klinikermelding-epj-web.azurewebsites.net",
            ),
        )

    @GetMapping
    fun index(model: Model): String {
        model["user"] = User("Dr. House O. Apartment", "92391831")

        return "epj"
    }

    @GetMapping("/partials/patient-picker")
    fun patientPicker(request: HttpServletRequest, model: Model): String {
        if (userState.get(request.session.id) != null) {
            model["fnr"] = userState.get(request.session.id)
            model["name"] =
                patients.find { it.fnr == userState.get(request.session.id) }?.name
                    ?: throw IllegalArgumentException(
                        "Unknown patient: ${userState.get(request.session.id)}"
                    )
            return "partials/consultation/start"
        }

        model["patients"] = patients

        return "partials/patient-picker"
    }

    @GetMapping("/partials/smart-picker")
    fun appsPicker(model: Model): String {
        model["apps"] = apps

        return "partials/smart-picker"
    }

    @GetMapping("/partials/launch")
    fun launchApp(@RequestParam app: String, model: Model): String {
        if (app == "none") {
            return "fragments/inner-default"
        }

        val appToLaunch =
            apps.find { it.clientId == app } ?: throw IllegalArgumentException("Unknown app: $app")

        val launchUrl =
            if (app == "localhost") {
                "${appToLaunch.url}/launch?iss=http://localhost:9000&launch=foo-bar-baz"
            } else {
                // TODO: code
                "${appToLaunch.url}/launch?iss=https://fhir.ekstern.dev.nav.no&launch=foo-bar-baz"
            }

        model["url"] = launchUrl
        model["name"] = appToLaunch.name

        return "partials/inner-launch"
    }
}

@Controller
@RequestMapping("/epj/consultation")
class ConsultationController(val userState: EpjUserState) {

    @PostMapping("/start")
    fun startConsultation(
        @RequestParam fnr: String,
        request: HttpServletRequest,
        model: Model,
    ): String {
        println("Starting consultation for $fnr (${request.session.id})")
        userState.save(request.session.id, fnr)

        model["fnr"] = fnr
        model["name"] =
            patients.find { it.fnr == fnr }?.name
                ?: throw IllegalArgumentException("Unknown patient: $fnr")

        // TODO set server context about the consultation
        return "partials/consultation/start"
    }

    @PostMapping("/end")
    fun endConsultation(request: HttpServletRequest, model: Model): String {
        println("Ending consultation")
        userState.unset(request.session.id)

        // TODO clear server context about the consultation
        return "partials/consultation/end"
    }
}
