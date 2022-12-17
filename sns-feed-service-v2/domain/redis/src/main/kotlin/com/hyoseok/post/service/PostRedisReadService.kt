package com.hyoseok.post.service

import com.hyoseok.post.dto.PostCacheDto
import com.hyoseok.post.entity.PostCache
import com.hyoseok.post.entity.PostCache.Companion.getPostIdKey
import com.hyoseok.post.entity.PostCache.Companion.getPostIdViewsKey
import com.hyoseok.post.entity.PostCache.Companion.getPostIdWishesKey
import com.hyoseok.post.repository.PostRedisPipelineRepository
import com.hyoseok.post.repository.PostRedisRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(prefix = "spring.post.redis", name = ["enable"], havingValue = "true")
class PostRedisReadService(
    private val postRedisRepository: PostRedisRepository,
    private val postRedisPipelineRepository: PostRedisPipelineRepository,
) {

    fun findPostCache(id: Long): PostCacheDto? {
        val postCache: PostCache =
            postRedisRepository.get(key = getPostIdKey(id = id), clazz = PostCache::class.java) ?: return null

        val postViewCache: Long =
            postRedisRepository.get(key = getPostIdViewsKey(id = id), clazz = Long::class.java) ?: 0L

        val postWishCache: Long =
            postRedisRepository.get(key = getPostIdWishesKey(id = id), clazz = Long::class.java) ?: 0L

        return PostCacheDto(postCache = postCache, viewCount = postViewCache, wishCount = postWishCache)
    }

    fun findPostCaches(ids: List<Long>): List<PostCacheDto> =
        if (ids.isNotEmpty()) {
            postRedisPipelineRepository.getPostCaches(ids = ids)
        } else {
            listOf()
        }

//    fun findPostCaches(memberIds: List<Long>, pageRequestByPosition: PageRequestByPosition): List<PostCacheDto> {
//        val (start: Long, size: Long) = pageRequestByPosition
//
//        if (start <= -1L || size == 0L) {
//            return listOf()
//        }
//
//        val keys: String = getPostIdsByMemberIdKey(memberId = memberId)
//        val end: Long = start.plus(size).minus(other = 1)
//        val nowDateTime: LocalDateTime = LocalDateTime.now().withNano(0)
//        val minScore: Double = Timestamp.valueOf(nowDateTime.minusDays(1)).time.toDouble()
//        val maxScore: Double = Timestamp.valueOf(nowDateTime).time.toDouble()
//        val postIds: List<Long> = postRedisRepository.zrevRangeByScore(
//            key = key,
//            minScore = minScore,
//            maxScore = maxScore,
//            start = start,
//            end = end,
//            clazz = Long::class.java,
//        )
//
//        if (postIds.isEmpty()) {
//            return listOf()
//        }
//
//        return postRedisPipelineRepository.getPostCaches(ids = postIds)
//    }
}
