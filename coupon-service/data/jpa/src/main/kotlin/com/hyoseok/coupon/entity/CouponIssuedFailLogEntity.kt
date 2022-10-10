package com.hyoseok.coupon.entity

import com.hyoseok.coupon.entity.enum.CouponIssuedFailLogApplicationType
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "coupon_issued_fail_log")
@DynamicUpdate
class CouponIssuedFailLogEntity private constructor(
    applicationType: String,
    data: String,
    errorMessage: String? = null,
    createdAt: LocalDateTime,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(name = "application_type", length = 50, nullable = false)
    var applicationType: String = applicationType
        protected set

    @Column(nullable = false)
    var data: String = data
        protected set

    @Column(name = "error_message", length = 2500, nullable = false)
    var errorMessage: String? = errorMessage
        protected set

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME")
    var createdAt: LocalDateTime = createdAt
        protected set

    companion object {
        operator fun invoke(couponIssuedFailLog: CouponIssuedFailLog) =
            with(receiver = couponIssuedFailLog) {
                CouponIssuedFailLogEntity(
                    applicationType = applicationType.name,
                    data = data,
                    errorMessage = errorMessage,
                    createdAt = createdAt,
                )
            }
    }

    fun toDomain() =
        CouponIssuedFailLog(
            id = id!!,
            applicationType = CouponIssuedFailLogApplicationType(value = applicationType),
            data = data,
            errorMessage = errorMessage,
            createdAt = createdAt,
        )
}