package com.hyoseok.wish.repository

import com.hyoseok.config.RedisEmbbededServerConfig
import com.hyoseok.config.WishRedisConfig
import com.hyoseok.config.WishRedisTemplateConfig
import com.hyoseok.wish.entity.WishCache
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration

@DataRedisTest
@DirtiesContext
@EnableAutoConfiguration
@ContextConfiguration(
    classes = [
        RedisEmbbededServerConfig::class,
        WishRedisConfig::class,
        WishRedisTemplateConfig::class,
        WishRedisRepositoryImpl::class,
        WishRedisPipelineRepositoryImpl::class,
        WishRedisTransactionRepositoryImpl::class,
        WishRedisRepository::class,
        WishRedisPipelineRepository::class,
        WishRedisTransactionRepository::class,
    ],
)
internal class WishRedisPipelineRepositoryTests : DescribeSpec() {

    override fun extensions(): List<Extension> = listOf(SpringExtension)
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf

    @Autowired
    @Qualifier("wishRedisTemplate")
    private lateinit var redisTemplate: RedisTemplate<String, String?>

    @Autowired
    private lateinit var wishRedisTransactionRepository: WishRedisTransactionRepository

    @Autowired
    private lateinit var wishRedisPipelineRepository: WishRedisPipelineRepository

    init {
        this.afterSpec {
            redisTemplate.delete(redisTemplate.keys("*"))
        }

        this.describe("getWishCountsMap 메서드는") {
            it("postIds 값을 통해 캐싱된 좋아요 리스트를 반환한다") {
                // given
                val memberId = 1L
                val postIds: List<Long> = (1L..10L).map { it }

                postIds.forEach {
                    wishRedisTransactionRepository.createWish(wishCache = WishCache(postId = it, memberId = memberId))
                }

                // when
                val wishCountsMap: Map<Long, Long> = wishRedisPipelineRepository.getWishCountsMap(postIds = postIds)

                // then
                wishCountsMap.shouldHaveSize(postIds.size)
                postIds.forEach {
                    wishCountsMap[it].shouldNotBeNull()
                    wishCountsMap[it].shouldBe(1)
                }
            }
        }
    }
}
