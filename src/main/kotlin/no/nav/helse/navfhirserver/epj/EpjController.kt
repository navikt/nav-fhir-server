package no.nav.helse.navfhirserver.epj

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("/epj")
class EpjController {

    private data class Patient(val name: String, val fnr: String)

    private val patients = listOf(
        Patient("Ola Nordmann", "45847100951"),
        Patient("Kari Karisdottir", "65927600603"),
    )

    @GetMapping
    fun index(model: Model): String {
        model["patients"] = patients

        return "epj"
    }

    @GetMapping("/partial/patient-picker")
    fun patientPicker(model: Model): String {
        model["patients"] = patients

        return "partials/patient-picker"
    }
}

