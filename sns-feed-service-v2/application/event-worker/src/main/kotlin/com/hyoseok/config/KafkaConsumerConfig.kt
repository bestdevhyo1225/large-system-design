package com.hyoseok.config

import org.apache.kafka.clients.consumer.ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_INSTANCE_ID_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties.AckMode
import java.net.InetAddress
import java.util.UUID

@Configuration
@EnableKafka
class KafkaConsumerConfig(
    @Value("\${spring.kafka.consumer.bootstrap-servers}")
    private val bootstrapServers: String,

    @Value("\${spring.kafka.consumer.auto-offset-reset}")
    private val autoOffsetReset: String,

    @Value("\${spring.kafka.consumer.session-timeout}")
    private val sessionTimeout: Int,

    @Value("\${spring.kafka.consumer.heartbeat-interval}")
    private val heartbeatInterval: Int,

    @Value("\${spring.kafka.consumer.max-poll-interval}")
    private val maxPollInterval: Int,

    @Value("\${spring.kafka.consumer.max-poll-records}")
    private val maxPollRecords: Int,

    @Value("\${spring.kafka.consumer.allow-auto-create-topics}")
    private val allowAutoCreateTopics: Boolean,

    @Value("\${spring.kafka.consumer.group-id}")
    private val groupId: String,
) {

    @Bean
    fun feedKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val containerFactory = ConcurrentKafkaListenerContainerFactory<String, String>()
        containerFactory.consumerFactory = DefaultKafkaConsumerFactory(configConsumerProps())
        containerFactory.containerProperties.ackMode = AckMode.MANUAL_IMMEDIATE // ?????? ?????? ??????
        return containerFactory
    }

    @Bean
    fun wishKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val containerFactory = ConcurrentKafkaListenerContainerFactory<String, String>()
        containerFactory.consumerFactory = DefaultKafkaConsumerFactory(configConsumerProps())
        containerFactory.containerProperties.ackMode = AckMode.MANUAL_IMMEDIATE // ?????? ?????? ??????
        return containerFactory
    }

    private fun configConsumerProps(): Map<String, Any> {
        val props: MutableMap<String, Any> = mutableMapOf()
        props[BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ENABLE_AUTO_COMMIT_CONFIG] = false // ?????? ??????
        props[AUTO_OFFSET_RESET_CONFIG] = autoOffsetReset
        props[SESSION_TIMEOUT_MS_CONFIG] = sessionTimeout
        props[HEARTBEAT_INTERVAL_MS_CONFIG] = heartbeatInterval
        props[MAX_POLL_INTERVAL_MS_CONFIG] = maxPollInterval
        props[MAX_POLL_RECORDS_CONFIG] = maxPollRecords
        props[ALLOW_AUTO_CREATE_TOPICS_CONFIG] = allowAutoCreateTopics
        /*
         * ????????? ????????? -> ????????? ?????? ????????? ???????????? ????????? ????????? ???????????? ???????????? ?????? ?????????????????? ??????????????? ???????????? ????????? ??????.
         * ????????? ????????? ????????? ?????? 'GROUP_INSTANCE_ID' ??? ????????????
         * ?????? session.timeout.ms??? ????????? ????????? ??????????????? ???????????? ????????? ?????? ????????? ???????????? ????????? ?????????
        * */
        props[GROUP_INSTANCE_ID_CONFIG] = "${InetAddress.getLocalHost().hostAddress}-${UUID.randomUUID()}"
        props[GROUP_ID_CONFIG] = groupId
        props[PARTITION_ASSIGNMENT_STRATEGY_CONFIG] = listOf(CooperativeStickyAssignor::class.java) // ????????? ????????? ????????? ?????? ??????
        props[KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        return props
    }
}
