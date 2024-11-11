package no.nav.helse.navfhirserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("no.nav.helse.navfhirserver")
class NavFhirServerApplication

fun main(args: Array<String>) {
    runApplication<NavFhirServerApplication>(*args)
}
