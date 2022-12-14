package com.hyoseok.post.service

import com.hyoseok.post.dto.PostCacheDto
import com.hyoseok.post.entity.PostCache
import com.hyoseok.post.entity.PostCache.Companion.getPostIdKey
import com.hyoseok.post.entity.PostCache.Companion.getPostIdsByMemberIdKey
import com.hyoseok.post.repository.PostRedisRepository
import com.hyoseok.util.PageRequestByPosition
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(prefix = "spring.post.redis", name = ["enable"], havingValue = "true")
class PostRedisReadService(
    private val postRedisRepository: PostRedisRepository,
) {

    fun findPostCache(id: Long): PostCacheDto? {
        val postCache: PostCache =
            postRedisRepository.get(key = getPostIdKey(id = id), clazz = PostCache::class.java) ?: return null

        return PostCacheDto(postCache = postCache)
    }

    fun findPostCaches(ids: List<Long>): Pair<List<PostCacheDto>, List<Long>> =
        getPostCacheDtosAndNotExistsPostIds(postIds = ids)

    fun findPostCaches(
        memberId: Long,
        pageRequestByPosition: PageRequestByPosition,
    ): Pair<List<PostCacheDto>, List<Long>> {
        val (start: Long, size: Long) = pageRequestByPosition
        val end: Long = start.plus(size).minus(other = 1)
        val postIds: List<Long> = postRedisRepository.zrevRange(
            key = getPostIdsByMemberIdKey(memberId = memberId),
            start = start,
            end = end,
            clazz = Long::class.java,
        )

        return getPostCacheDtosAndNotExistsPostIds(postIds = postIds)
    }

    private fun getPostCacheDtosAndNotExistsPostIds(postIds: List<Long>): Pair<List<PostCacheDto>, List<Long>> {
        if (postIds.isEmpty()) {
            return Pair(first = listOf(), second = listOf())
        }

        val keys: List<String> = postIds.map { getPostIdKey(id = it) }
        val postCaches: List<PostCache> = postRedisRepository.mget(keys = keys)
        val postCacheDtos: List<PostCacheDto> = postCaches.map { PostCacheDto(postCache = it) }
        val postCacheIdMap: Map<Long, Boolean> = postCacheDtos.associate { it.id to true }
        val notExistsPostIds: List<Long> = postIds.filter { postCacheIdMap[it] == null || postCacheIdMap[it] == false }

        return Pair(first = postCacheDtos, second = notExistsPostIds)
    }
}
