package com.hyoseok.repository.post

import com.hyoseok.config.RedisExpireTimes.POST
import com.hyoseok.config.RedisExpireTimes.POST_VIEWS
import com.hyoseok.config.RedisKeys
import com.hyoseok.config.RedisKeys.POST_KEYS
import com.hyoseok.config.post.RedisPostConfig
import com.hyoseok.config.post.RedisPostEmbbededServerConfig
import com.hyoseok.config.post.RedisPostTemplateConfig
import com.hyoseok.post.entity.PostCache
import com.hyoseok.post.entity.PostImage
import com.hyoseok.post.repository.PostCacheReadRepository
import com.hyoseok.post.repository.PostCacheRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.data.redis.RedisSystemException
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit.SECONDS

@DataRedisTest
@DirtiesContext
@EnableAutoConfiguration
@ContextConfiguration(
    classes = [
        RedisPostEmbbededServerConfig::class,
        RedisPostConfig::class,
        RedisPostTemplateConfig::class,
        PostCacheRepository::class,
        PostCacheReadRepository::class,
        PostCacheRepositoryImpl::class,
        PostCacheReadRepositoryImpl::class,
    ],
)
class PostCacheRepositoryTests : DescribeSpec() {

    override fun extensions(): List<Extension> = listOf(SpringExtension)
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf

    @Autowired
    private lateinit var postCacheRepository: PostCacheRepository

    @Autowired
    private lateinit var postCacheReadRepository: PostCacheReadRepository

    init {
        this.describe("increment ????????????") {
            it("key??? value??? 1??? ?????? ?????????") {
                // given
                val id = 1L
                val key: String = RedisKeys.getPostViewsKey(id = id)
                val value = 0L

                postCacheRepository.set(key = key, value = value, expireTime = POST_VIEWS, timeUnit = SECONDS)

                // when
                val result: Long = postCacheRepository.increment(key = key)

                // then
                result.shouldBe(value.plus(1))
            }

            context("key??? ???????????? ?????? ??????") {
                it("value??? ?????? ?????? 1??????") {
                    // given
                    val id = 1L
                    val key: String = RedisKeys.getPostViewsKey(id = id)

                    // when
                    val result: Long = postCacheRepository.increment(key = key)

                    // then
                    result.shouldBe(1)
                }
            }

            context("value??? ???????????? ?????? ??????") {
                it("????????? ?????????") {
                    val id = 1L
                    val key: String = RedisKeys.getPostViewsKey(id = id)
                    val value = "testValue"

                    postCacheRepository.set(key = key, value = value, expireTime = POST_VIEWS, timeUnit = SECONDS)

                    shouldThrow<RedisSystemException> { postCacheRepository.increment(key = key) }
                }
            }
        }

        this.describe("set ????????????") {
            it("PostCache ???????????? ????????????") {
                // given
                val id = 1L
                val key: String = RedisKeys.getPostKey(id = id)
                val snsCache = PostCache(
                    id = id,
                    memberId = 1234L,
                    title = "title",
                    contents = "contents",
                    writer = "writer",
                    createdAt = LocalDateTime.now().withNano(0),
                    updatedAt = LocalDateTime.now().withNano(0),
                    images = listOf(PostImage(id = 1L, url = "https://test.com", sortOrder = 0)),
                )

                // when
                postCacheRepository.set(key = key, value = snsCache, expireTime = POST, timeUnit = SECONDS)

                // then
                postCacheReadRepository.get(key = key, clazz = PostCache::class.java)
                    .shouldBe(snsCache)
            }
        }

        this.describe("zadd ????????????") {
            it("key, value, score??? ????????????") {
                // given
                val key: String = POST_KEYS
                val values = listOf(RedisKeys.getPostKey(id = 1L), RedisKeys.getPostKey(id = 2L))
                val nowDateTime = LocalDateTime.now().withNano(0)
                val scores = listOf(
                    Timestamp.valueOf(nowDateTime).time.toDouble(),
                    Timestamp.valueOf(nowDateTime.plusHours(1)).time.toDouble(),
                )

                // when
                postCacheRepository.zadd(key = key, value = values[0], score = scores[0])
                postCacheRepository.zadd(key = key, value = values[1], score = scores[1])

                // then
                postCacheReadRepository.zrevrange(key = key, start = 0, end = 1, clazz = String::class.java)
                    .containsAll(values)
            }
        }
    }
}
