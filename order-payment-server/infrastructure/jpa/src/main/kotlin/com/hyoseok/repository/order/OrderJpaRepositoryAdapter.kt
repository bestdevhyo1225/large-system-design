package com.hyoseok.repository.order

import com.hyoseok.entity.order.OrderJpaEntity
import com.hyoseok.exception.InfrastructureExceptionMessage.FAIL_CHANGE_ORDER_STATUS
import com.hyoseok.exception.InfrastructureExceptionMessage.NOT_FOUND_ORDER
import com.hyoseok.order.entity.Order
import com.hyoseok.order.entity.enums.OrderStatus
import com.hyoseok.order.repository.OrderRepository
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class OrderJpaRepositoryAdapter(
    private val orderJpaRepository: OrderJpaRepository,
) : OrderRepository {

    private val logger = KotlinLogging.logger {}

    @Transactional
    override fun save(order: Order) {
        val orderJpaEntity = OrderJpaEntity(order = order)
        orderJpaRepository.save(orderJpaEntity)
        orderJpaEntity.mapDomainEntityId(order = order)
    }

    @Transactional
    override fun updateStatus(order: Order) =
        with(receiver = order) {
            if (orderJpaRepository.updateStatus(status = status, updatedAt = updatedAt, id = id) <= 0) {
                logger.error { FAIL_CHANGE_ORDER_STATUS }
            }
        }

    @Transactional
    override fun updateStatus(id: Long, status: OrderStatus) {
        val orderJpaEntity = findEntityById(id = id)
        val orderDomainEntity = orderJpaEntity.toDomainEntity()
        orderDomainEntity.changeStatus(status = status)
        orderJpaEntity.changeStatus(status = orderDomainEntity.status)
    }

    override fun find(id: Long): Order = findEntityById(id = id).toDomainEntity()

    private fun findEntityById(id: Long): OrderJpaEntity =
        orderJpaRepository.findByIdOrNull(id = id) ?: throw NoSuchElementException(NOT_FOUND_ORDER)
}
