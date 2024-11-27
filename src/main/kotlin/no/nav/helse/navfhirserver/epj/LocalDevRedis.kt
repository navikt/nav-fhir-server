package no.nav.helse.navfhirserver.epj

import com.redis.testcontainers.RedisContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.testcontainers.utility.DockerImageName

@Configuration
@Profile("local")
class LocalDevRedis {

    @Bean(destroyMethod = "stop")
    fun redisContainer(): RedisContainer {
        val redis = RedisContainer(DockerImageName.parse("redis:6.2.6"))
        redis.start()
        return redis
    }

    @Bean
    fun redisConnectionFactory(redisContainer: RedisContainer): LettuceConnectionFactory {
        val config = RedisStandaloneConfiguration()
        config.hostName = redisContainer.host
        config.port = redisContainer.getMappedPort(6379)
        return LettuceConnectionFactory(config)
    }
}
