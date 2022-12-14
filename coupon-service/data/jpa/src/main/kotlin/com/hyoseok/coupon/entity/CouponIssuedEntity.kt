package com.hyoseok.coupon.entity

import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(
    name = "coupon_issued",
    indexes = [
        Index(name = "uk_coupon_id_member_id", columnList = "coupon_id,member_id", unique = true),
    ],
)
@DynamicUpdate
class CouponIssuedEntity private constructor(
    couponId: Long,
    memberId: Long,
    createdAt: LocalDateTime,
    deletedAt: LocalDateTime? = null,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(name = "coupon_id", nullable = false)
    var couponId: Long = couponId
        protected set

    @Column(name = "member_id", nullable = false)
    var memberId: Long = memberId
        protected set

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME")
    var createdAt: LocalDateTime = createdAt
        protected set

    @Column(name = "deleted_at", columnDefinition = "DATETIME")
    var deletedAt: LocalDateTime? = deletedAt
        protected set

    companion object {
        operator fun invoke(couponIssued: CouponIssued) =
            with(receiver = couponIssued) {
                CouponIssuedEntity(
                    couponId = couponId,
                    memberId = memberId,
                    createdAt = createdAt,
                    deletedAt = deletedAt,
                )
            }
    }

    fun toDomain() =
        CouponIssued(id = id!!, couponId = couponId, memberId = memberId, createdAt = createdAt, deletedAt = deletedAt)
}
