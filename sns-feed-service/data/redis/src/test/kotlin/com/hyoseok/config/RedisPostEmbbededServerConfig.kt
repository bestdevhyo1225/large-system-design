package com.hyoseok.config

import com.hyoseok.config.post.RedisPostServerProperties
import mu.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import redis.embedded.RedisServer
import redis.embedded.exceptions.EmbeddedRedisException
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Configuration
@EnableConfigurationProperties(value = [RedisPostServerProperties::class])
class RedisPostEmbbededServerConfig(
    private val redisPostServerProperties: RedisPostServerProperties,
) {

    private val logger = KotlinLogging.logger {}

    private lateinit var embeddedRedisPostServer: RedisServer

    @PostConstruct
    fun startEmbeddedRedisServer() {
        try {
            val postSplits: List<String> = redisPostServerProperties.nodes.values.first().first().split(":")
            embeddedRedisPostServer = RedisServer(postSplits[1].toInt())
            embeddedRedisPostServer.start()
        } catch (exception: EmbeddedRedisException) {
            logger.error { exception }
        }
    }

    @PreDestroy
    fun stopEmbeddedRedisServer() {
        try {
            embeddedRedisPostServer.stop()
        } catch (exception: EmbeddedRedisException) {
            logger.error { exception }
        }
    }
}