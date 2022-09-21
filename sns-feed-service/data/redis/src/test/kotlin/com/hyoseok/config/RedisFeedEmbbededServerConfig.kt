package com.hyoseok.config

import com.hyoseok.config.feed.RedisFeedServerProperties
import mu.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import redis.embedded.RedisServer
import redis.embedded.exceptions.EmbeddedRedisException
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Configuration
@EnableConfigurationProperties(value = [RedisFeedServerProperties::class])
class RedisFeedEmbbededServerConfig(
    private val redisFeedServerProperties: RedisFeedServerProperties,
) {

    private val logger = KotlinLogging.logger {}

    private lateinit var embeddedRedisFeedServer: RedisServer

    @PostConstruct
    fun startEmbeddedRedisServer() {
        try {
            val feedSplits: List<String> = redisFeedServerProperties.nodes.values.first().first().split(":")

            embeddedRedisFeedServer = RedisServer(feedSplits[1].toInt())

            embeddedRedisFeedServer.start()
        } catch (exception: EmbeddedRedisException) {
            logger.error { exception }
        }
    }

    @PreDestroy
    fun stopEmbeddedRedisServer() {
        try {
            embeddedRedisFeedServer.stop()
        } catch (exception: EmbeddedRedisException) {
            logger.error { exception }
        }
    }
}
