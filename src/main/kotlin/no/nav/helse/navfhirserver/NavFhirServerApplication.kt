package no.nav.helse.navfhirserver

import no.nav.helse.navfhirserver.Cluster.Companion.profiler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@EnableWebMvc
@ConfigurationPropertiesScan("no.nav.helse.navfhirserver")
@SpringBootApplication
@EnableWebSecurity(debug = true)
open class NavFhirServerApplication

fun main(args: Array<String>) {
    runApplication<NavFhirServerApplication>(*args)  {
        setAdditionalProfiles(*profiler)
    }
}

