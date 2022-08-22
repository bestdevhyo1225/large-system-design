package com.bestdev.order

import com.bestdev.order.entity.Order
import com.bestdev.order.entity.enums.OrderStatus
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.longs.shouldBeZero
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class OrderTests : DescribeSpec(
    {
        describe("invoke 메서드는") {
            it("Order 엔티티를 생성한다.") {
                val order = Order()
                order.id.shouldBeZero()
                order.status.shouldBe(OrderStatus.WAIT)
                order.orderedAt.shouldNotBeNull()
                order.updatedAt.shouldNotBeNull()
            }
        }
    },
)