package com.bestdev.config.jpa

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EntityScan(value = ["com.bestdev.entity"])
@EnableJpaRepositories(
    basePackages = ["com.bestdev.repository"],
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager",
)
class JpaConfig(
    @Value("\${spring.jpa.open-in-view}")
    private val openInView: Boolean,
) {

    @Bean
    @ConfigurationProperties(prefix = "spring.jpa")
    fun jpaProperties(): JpaProperties {
        return JpaProperties()
    }

    @Bean
    fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        val properties = jpaProperties().properties.toProperties()
        properties["open-in-view"] = openInView

        val entityManagerFactoryBean = LocalContainerEntityManagerFactoryBean()
        entityManagerFactoryBean.dataSource = dataSource
        entityManagerFactoryBean.jpaVendorAdapter = HibernateJpaVendorAdapter()
        entityManagerFactoryBean.persistenceUnitName = "CustomEntityManager"
        entityManagerFactoryBean.setPackagesToScan("com.bestdev")
        entityManagerFactoryBean.setJpaProperties(properties)

        return entityManagerFactoryBean
    }

    @Bean
    fun transactionManager(dataSource: DataSource): PlatformTransactionManager {
        val jpaTransactionManager = JpaTransactionManager()
        jpaTransactionManager.entityManagerFactory = entityManagerFactory(dataSource = dataSource).`object`

        return jpaTransactionManager
    }
}