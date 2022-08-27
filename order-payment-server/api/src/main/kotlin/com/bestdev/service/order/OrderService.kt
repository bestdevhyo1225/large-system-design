package com.bestdev.service.order

import com.bestdev.order.entity.Order
import com.bestdev.order.entity.enums.OrderStatus
import com.bestdev.order.repository.OrderRepository
import com.bestdev.order.repository.read.OrderReadRepository
import com.bestdev.service.dto.CreateOrderDto
import com.bestdev.service.dto.CreateOrderResultDto
import com.bestdev.service.dto.CreateSubInfoOfOrderDto
import com.bestdev.service.dto.event.CreatedOrderEventDto
import com.bestdev.shipping.repository.ShippingRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderReadRepository: OrderReadRepository,
    private val shippingRepository: ShippingRepository,
    private val orderApplicationEventPublisher: ApplicationEventPublisher,
) {

    fun create(dto: CreateOrderDto): CreateOrderResultDto {
        val order: Order = dto.toDomainEntity()

        orderRepository.save(order = order)
        orderApplicationEventPublisher.publishEvent(CreatedOrderEventDto(order = order))

        return CreateOrderResultDto(order = order)
    }

    fun createOrderShipping(dto: CreateSubInfoOfOrderDto) {
        val order: Order = orderReadRepository.findWithItems(id = dto.orderId)
        order.changeStatus(status = OrderStatus.COMPLETE)
        orderRepository.updateStatus(order = order)

        shippingRepository.saveAll(shippings = order.createShippings())
    }

    fun updateStatus(id: Long, status: String) {
        val order: Order = orderRepository.find(id = id)
        order.changeStatus(status = OrderStatus(value = status))
        orderRepository.updateStatus(order = order)
    }
}