package com.hyoseok.usecase

import com.hyoseok.exception.ApiRateLimitException
import com.hyoseok.feed.dto.FeedEventDto
import com.hyoseok.feed.producer.FeedKafkaProducer
import com.hyoseok.follow.dto.FollowDto
import com.hyoseok.follow.service.FollowReadService
import com.hyoseok.member.dto.MemberDto
import com.hyoseok.member.service.MemberReadService
import com.hyoseok.post.dto.PostCacheDto
import com.hyoseok.post.dto.PostCreateDto
import com.hyoseok.post.dto.PostDto
import com.hyoseok.post.dto.PostImageCacheDto
import com.hyoseok.post.service.PostRedisService
import com.hyoseok.post.service.PostService
import com.hyoseok.usecase.dto.CreatePostUsecaseDto
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class CreatePostUsecase(
    private val feedKafkaProducer: FeedKafkaProducer,
    private val followReadService: FollowReadService,
    private val memberReadService: MemberReadService,
    private val postService: PostService,
    private val postRedisService: PostRedisService,
) {

    private val logger = KotlinLogging.logger {}

    @RateLimiter(name = "createPostUsecase", fallbackMethod = "fallbackExecute")
    fun execute(createPostUsecaseDto: CreatePostUsecaseDto): PostDto {
        val memberDto: MemberDto = memberReadService.findMember(id = createPostUsecaseDto.memberId)
        val postCreateDto: PostCreateDto = createPostUsecaseDto.toDomainDto(memberDto = memberDto)
        val postDto: PostDto = postService.create(dto = postCreateDto)

        CoroutineScope(context = Dispatchers.IO).launch {
            createPostCache(postDto = postDto)
            sendFeedEvent(postDto = postDto, influencer = memberDto.influencer)
        }

        return postDto
    }

    private suspend fun createPostCache(postDto: PostDto) {
        with(receiver = postDto) {
            postRedisService.createOrUpdate(
                dto = PostCacheDto(
                    id = id,
                    memberId = memberId,
                    title = title,
                    contents = contents,
                    writer = writer,
                    wishCount = wishCount,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                    images = images.map { PostImageCacheDto(id = it.id, url = it.url, sortOrder = it.sortOrder) },
                ),
            )
        }
    }

    private suspend fun sendFeedEvent(postDto: PostDto, influencer: Boolean) {
        if (influencer) {
            return
        }

        var lastId = 0L
        val limit = 1_000L

        while (true) {
            val followDtos: List<FollowDto> = followReadService.findFollowers(
                followeeId = postDto.memberId,
                lastId = lastId,
                limit = limit,
            )

            if (followDtos.isEmpty()) {
                break
            }

            followDtos.forEach {
                feedKafkaProducer.sendAsync(event = FeedEventDto(postId = postDto.id, followerId = it.followerId))
            }

            lastId = followDtos.last().id
        }
    }

    private fun fallbackExecute(exception: Exception): PostDto {
        logger.error { exception.localizedMessage }
        throw ApiRateLimitException(message = "일시적으로 게시물을 등록할 수 없습니다. 잠시 후에 다시 시도해주세요.")
    }
}
