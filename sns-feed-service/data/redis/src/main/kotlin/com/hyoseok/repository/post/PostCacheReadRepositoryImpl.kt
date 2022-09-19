package com.hyoseok.repository.post

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hyoseok.post.repository.PostCacheReadRepository
import com.hyoseok.repository.AbstractCacheRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class PostCacheReadRepositoryImpl(
    @Qualifier("redisPostTemplate")
    private val redisTemplate: RedisTemplate<String, String?>,
) : PostCacheReadRepository, AbstractCacheRepository() {

    private val jacksonObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    override fun <T> get(key: String, clazz: Class<T>): T? {
        val remainingExpiryTimeMS: Long = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS)

        if (isRefreshKey(remainingExpiryTimeMS = remainingExpiryTimeMS)) {
            return null
        }

        val value: String? = redisTemplate.opsForValue().get(key)

        if (value.isNullOrBlank()) {
            return null
        }

        return jacksonObjectMapper.readValue(value, clazz)
    }
}
