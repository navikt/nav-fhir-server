package no.nav.helse.navfhirserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@EnableWebMvc
@ConfigurationPropertiesScan("no.nav.helse.navfhirserver")
@SpringBootApplication
class NavFhirServerApplication

fun main(args: Array<String>) {
    runApplication<NavFhirServerApplication>(*args)
}
