package com.hyoseok.service.post

import com.hyoseok.config.KafkaTopics
import com.hyoseok.config.RedisPostCommons.ZSET_POST_MAX_LIMIT
import com.hyoseok.config.RedisPostExpireTimes.POST
import com.hyoseok.config.RedisPostExpireTimes.POST_VIEWS
import com.hyoseok.config.RedisPostKeys
import com.hyoseok.config.RedisPostKeys.POST_KEYS
import com.hyoseok.follow.entity.Follow
import com.hyoseok.follow.repository.FollowReadRepository
import com.hyoseok.member.repository.MemberReadRepository
import com.hyoseok.post.entity.Post
import com.hyoseok.post.entity.PostCache
import com.hyoseok.post.repository.PostCacheRepository
import com.hyoseok.post.repository.PostRepository
import com.hyoseok.publisher.KafkaProducer
import com.hyoseok.service.dto.FollowerSendEventDto
import com.hyoseok.service.dto.PostCreateDto
import com.hyoseok.service.dto.PostCreateResultDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit.SECONDS

@Service
@Transactional
class PostCreateService(
    private val postRepository: PostRepository,
    private val postCacheRepository: PostCacheRepository,
    private val followReadRepository: FollowReadRepository,
    private val memberReadRepository: MemberReadRepository,
    private val kafkaProducer: KafkaProducer,
) {

    fun execute(dto: PostCreateDto): PostCreateResultDto {
        val post: Post = dto.toEntity()

        if (memberReadRepository.exists(id = dto.memberId)) {
            postRepository.save(post = post)
        }

        CoroutineScope(context = Dispatchers.IO).launch {
            setPostCache(id = post.id!!, postCache = post.toPostCache())
            setPostViewCount(id = post.id!!, viewCount = post.viewCount)
            zaddPostKeys(id = post.id!!, createdAt = post.createdAt)
            zremPostKeysRangeByRank()
            sendFeedToFollower(post = post, followeeId = post.memberId)
        }

        return PostCreateResultDto(post = post)
    }

    private suspend fun setPostCache(id: Long, postCache: PostCache) {
        postCacheRepository.set(
            key = RedisPostKeys.getPostKey(id = id),
            value = postCache,
            expireTime = POST,
            timeUnit = SECONDS,
        )
    }

    private suspend fun setPostViewCount(id: Long, viewCount: Long) {
        postCacheRepository.set(
            key = RedisPostKeys.getPostViewsKey(id = id),
            value = viewCount,
            expireTime = POST_VIEWS,
            timeUnit = SECONDS,
        )
    }

    private suspend fun zaddPostKeys(id: Long, createdAt: LocalDateTime) {
        postCacheRepository.zadd(
            key = POST_KEYS,
            value = id,
            score = Timestamp.valueOf(createdAt).time.toDouble(),
        )
    }

    private suspend fun zremPostKeysRangeByRank() {
        postCacheRepository.zremRangeByRank(key = POST_KEYS, start = ZSET_POST_MAX_LIMIT, end = ZSET_POST_MAX_LIMIT)
    }

    private suspend fun sendFeedToFollower(post: Post, followeeId: Long) {
        val limit = 1000L
        var offset = 0L
        var isProgress = true
        while (isProgress) {
            val (total: Long, follows: List<Follow>) = followReadRepository.findAllByFolloweeIdAndLimitAndOffset(
                followeeId = followeeId,
                limit = limit,
                offset = offset,
            )

            follows.forEach {
                kafkaProducer.send(
                    event = FollowerSendEventDto(post = post, followerId = it.followerId),
                    topic = KafkaTopics.SNS_FEED,
                )
            }

            offset += limit

            if (offset >= total) {
                isProgress = false
            }
        }
    }
}