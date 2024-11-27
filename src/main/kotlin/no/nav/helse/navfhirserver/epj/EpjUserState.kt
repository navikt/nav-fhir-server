package no.nav.helse.navfhirserver.epj

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class EpjUserState(private val redisTemplate: RedisTemplate<String, String>) {
    fun save(key: String, value: String) = redisTemplate.opsForValue().set("session:$key", value)

    fun unset(key: String) = redisTemplate.delete("session:$key")

    fun get(key: String): String? = redisTemplate.opsForValue().get("session:$key")
}
