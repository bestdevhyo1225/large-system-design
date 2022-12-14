package com.hyoseok.config

import com.hyoseok.config.RedisMode.Cluster
import com.hyoseok.config.RedisMode.Replication
import com.hyoseok.config.RedisMode.Standalone
import io.lettuce.core.ReadFrom
import mu.KotlinLogging
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisClusterConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration
import java.time.Duration

@Configuration
@EnableCaching(proxyTargetClass = true)
@ConditionalOnProperty(prefix = "spring.post.redis", name = ["enable"], havingValue = "true")
class PostRedisConfig(
    @Value("\${spring.post.redis.mode}")
    private val mode: RedisMode,

    @Value("\${spring.post.redis.nodes}")
    private val nodes: List<String>,

    @Value("\${spring.post.redis.lettuce.pool.max-active}")
    private val maxActive: Int,

    @Value("\${spring.post.redis.lettuce.pool.max-idle}")
    private val maxIdle: Int,

    @Value("\${spring.post.redis.lettuce.pool.max-idle}")
    private val minIdle: Int,

    @Value("\${spring.post.redis.lettuce.pool.max-wait}")
    private val maxWait: Long,
) {

    private val logger = KotlinLogging.logger {}

    @Bean
    @Primary
    fun postRedisConnectionFactory(): RedisConnectionFactory {
        val lettuceConnectionFactory: LettuceConnectionFactory = when (mode) {
            Standalone -> {
                logger.info { "post redis standalone mode" }

                val (host: String, port: Int) = getHostAndPort()
                LettuceConnectionFactory(RedisStandaloneConfiguration(host, port), lettuceClientConfig())
            }

            Replication -> {
                logger.info { "post redis primary-replica mode" }

                val (host: String, port: Int) = getHostAndPort()
                val staticMasterReplicaConfiguration = RedisStaticMasterReplicaConfiguration(host, port)
                setReplicaNodes(staticMasterReplicaConfiguration = staticMasterReplicaConfiguration)
                LettuceConnectionFactory(staticMasterReplicaConfiguration, lettuceClientConfig())
            }

            Cluster -> {
                logger.info { "post redis cluster mode" }

                LettuceConnectionFactory(RedisClusterConfiguration(nodes), lettuceClientConfig())
            }
        }

        return lettuceConnectionFactory.apply {
            // RedisClient.connect ?????? ???????????? ???????????????, EagerInitialization ??? true??? ???????????? ????????? ??? ??????.
            eagerInitialization = true
        }
    }

    private fun setReplicaNodes(staticMasterReplicaConfiguration: RedisStaticMasterReplicaConfiguration) {
        nodes.drop(n = 1).forEach { replicaNode ->
            val (replicaHost: String, replicaPort: String) = replicaNode.split(":")
            staticMasterReplicaConfiguration.addNode(replicaHost, replicaPort.toInt())
        }
    }

    /*
    * [ Redis Connection Pool??? ???????????? ?????? ]
    * - Redis??? multi ???????????? ???????????? ??????????????? ????????? ??? ??????.
    * - ??????????????? ??? ??? ???????????? ???????????? ????????????. ?????? ?????? ????????? ????????????.
    * - ?????? ?????? ????????? ????????? ?????? ??????????????? ??????.
    * */
    private fun lettucePoolingClientConfiguration(): LettucePoolingClientConfiguration {
        val poolConfig = GenericObjectPoolConfig<Any>()
        poolConfig.maxTotal = maxActive
        poolConfig.maxIdle = maxIdle
        poolConfig.minIdle = minIdle
        poolConfig.setMaxWait(Duration.ofMillis(maxWait))

        return LettucePoolingClientConfiguration.builder()
            .clientName("post-redis-pooling-client")
            .readFrom(ReadFrom.REPLICA_PREFERRED)
            .poolConfig(poolConfig)
            .build()
    }

    private fun lettuceClientConfig(): LettuceClientConfiguration =
        LettuceClientConfiguration.builder()
            .clientName("post-redis-client")
            .readFrom(ReadFrom.REPLICA_PREFERRED)
            .build()

    private fun getHostAndPort(): Pair<String, Int> {
        val splits: List<String> = nodes.first().split(":")
        return Pair(first = splits[0], second = splits[1].toInt())
    }
}
