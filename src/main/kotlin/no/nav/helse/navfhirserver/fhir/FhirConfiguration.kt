package no.nav.helse.navfhirserver.fhir

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.StreamWriteConstraints
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class FhirConfiguration {

    companion object {
        private val FHIR_JSON = MediaType("application", "fhir+json")
    }

    @Bean
    fun fhirContext(): FhirContext {
        return FhirContext.forR4()
    }

    @Bean
    fun fhirJsonParser(fhirContext: FhirContext): IParser {
        val newJsonParser = fhirContext.newJsonParser()
        newJsonParser.setPrettyPrint(true)
        return newJsonParser
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        val streamWriteConstraints =
            StreamWriteConstraints.builder().maxNestingDepth(10_000).build()
        val jsonFactory =
            JsonFactory.builder().streamWriteConstraints(streamWriteConstraints).build()
        return ObjectMapper(jsonFactory).apply {
            registerModule(JavaTimeModule())
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            setDefaultLeniency(true)
        }
    }

    @Bean
    fun webClient(objectMapper: ObjectMapper): WebClient {
        val size = 16 * 1024 * 1024 // 16 MB
        val strategies =
            ExchangeStrategies.builder()
                .codecs { configurer ->
                    configurer
                        .defaultCodecs()
                        .jackson2JsonDecoder(
                            Jackson2JsonDecoder(objectMapper, FHIR_JSON).apply {
                                maxInMemorySize = size
                            })
                    configurer
                        .defaultCodecs()
                        .jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper, FHIR_JSON))
                }
                .build()
        return WebClient.builder().exchangeStrategies(strategies).build()
    }

    @Bean
    fun mappingJackson2HttpMessageConverter(
        objectMapper: ObjectMapper
    ): MappingJackson2HttpMessageConverter {
        return MappingJackson2HttpMessageConverter(objectMapper)
    }
}
