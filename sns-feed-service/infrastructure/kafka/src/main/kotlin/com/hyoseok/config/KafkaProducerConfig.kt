package com.hyoseok.config

import org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.BUFFER_MEMORY_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.COMPRESSION_TYPE_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate

@Configuration
@EnableKafka
@ConditionalOnProperty(prefix = "infrastructure.enable", name = ["kafka"], havingValue = "true")
class KafkaProducerConfig(
    @Value("\${infrastructure.kafka.producer.bootstrap-servers}")
    private val bootstrapServers: String,

    @Value("\${infrastructure.kafka.producer.acks}")
    private val acks: String,

    @Value("\${infrastructure.kafka.producer.buffer-memory}")
    private val bufferMemory: Long,

    @Value("\${infrastructure.kafka.producer.compression-type}")
    private val compressionType: String,

    @Value("\${infrastructure.kafka.producer.retries}")
    private val retries: Int,

    @Value("\${infrastructure.kafka.producer.batch-size}")
    private val batchSize: Int,

    @Value("\${infrastructure.kafka.producer.linger}")
    private val linger: Int,

    @Value("\${infrastructure.kafka.producer.request-timeout-ms}")
    private val requestTimeoutMs: Long,
) {

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        val props = mutableMapOf<String, Any>()

        props[BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ACKS_CONFIG] = acks
        props[BUFFER_MEMORY_CONFIG] = bufferMemory
        props[COMPRESSION_TYPE_CONFIG] = compressionType
        props[RETRIES_CONFIG] = retries
        props[BATCH_SIZE_CONFIG] = batchSize
        props[LINGER_MS_CONFIG] = linger
        props[REQUEST_TIMEOUT_MS_CONFIG] = requestTimeoutMs
        props[KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java

        return KafkaTemplate(DefaultKafkaProducerFactory(props))
    }
}
