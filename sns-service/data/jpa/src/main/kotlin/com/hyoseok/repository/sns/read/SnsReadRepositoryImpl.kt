package com.hyoseok.repository.sns.read

import com.hyoseok.entity.sns.QSnsImageJpaEntity.snsImageJpaEntity
import com.hyoseok.entity.sns.QSnsJpaEntity.snsJpaEntity
import com.hyoseok.entity.sns.QSnsTagJpaEntity.snsTagJpaEntity
import com.hyoseok.entity.sns.SnsJpaEntity
import com.hyoseok.entity.sns.SnsTagJpaEntity
import com.hyoseok.exception.Message.NOT_FOUND_SNS
import com.hyoseok.repository.sns.SnsJpaRepository
import com.hyoseok.sns.entity.Sns
import com.hyoseok.sns.repository.read.SnsReadRepository
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class SnsReadRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
    private val snsJpaRepository: SnsJpaRepository,
) : SnsReadRepository {

    override fun findById(snsId: Long): Sns {
        val snsJpaEntity: SnsJpaEntity = jpaQueryFactory
            .selectFrom(snsJpaEntity)
            .where(
                snsJpaEntityIdEq(snsId = snsId),
                snsJpaEntityDeletedAtIsNull(),
            )
            .fetchOne() ?: throw NoSuchElementException(NOT_FOUND_SNS)

        return snsJpaEntity.toDomainEntity()
    }

    override fun findWithAssociatedEntitiesById(snsId: Long): Sns {
        val snsJpaEntity: SnsJpaEntity = jpaQueryFactory
            .selectFrom(snsJpaEntity)
            .innerJoin(snsJpaEntity.snsImageJpaEntities, snsImageJpaEntity).fetchJoin()
            .where(
                snsJpaEntityIdEq(snsId = snsId),
                snsJpaEntityDeletedAtIsNull(),
            )
            .fetchOne() ?: throw NoSuchElementException(NOT_FOUND_SNS)

        val snsTagJpaEntities: List<SnsTagJpaEntity> = jpaQueryFactory
            .selectFrom(snsTagJpaEntity)
            .where(snsJpaEntityIdEq(snsId = snsId))
            .fetch()

        return snsJpaEntity.toDomainEntityAssociatedEntities(snsTagJpaEntities = snsTagJpaEntities)
    }

    override fun findAllByLimitAndOffset(limit: Long, offset: Long): Pair<List<Sns>, Long> {
        val snsJpaEntities: List<SnsJpaEntity> = jpaQueryFactory
            .selectFrom(snsJpaEntity)
            .where(snsJpaEntityDeletedAtIsNull())
            .orderBy(snsJpaEntity.createdAt.desc())
            .limit(limit)
            .offset(offset)
            .fetch()

        val totalCount: Long = snsJpaRepository.countExcludeDeletedSnsJpaEntity()

        return Pair(first = snsJpaEntities.map { it.toDomainEntityAssociatedEntities() }, second = totalCount)
    }

    private fun snsJpaEntityIdEq(snsId: Long): BooleanExpression = snsJpaEntity.id.eq(snsId)

    private fun snsJpaEntityDeletedAtIsNull(): BooleanExpression = snsJpaEntity.deletedAt.isNull
}
