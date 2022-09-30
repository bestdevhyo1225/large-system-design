package com.hyoseok.follow

import com.hyoseok.exception.DomainExceptionMessage.FAIL_ADD_FOLLOWEE
import com.hyoseok.exception.DomainExceptionMessage.INVALID_FOLLOWER_ID_FOLLOWEE_ID
import com.hyoseok.follow.entity.Follow
import com.hyoseok.follow.entity.Follow.Companion.MAX_FOLLOWEE_LIMIT
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

internal class FollowTests : DescribeSpec(
    {
        describe("checkFolloweeCount 메서드는") {
            context("MAX 값을 넘어가는 경우") {
                it("예외를 던진다") {
                    // given
                    val followeeCount: Long = MAX_FOLLOWEE_LIMIT.plus(1)

                    // when
                    val exception: RuntimeException = shouldThrow { Follow.checkFolloweeCount(value = followeeCount) }

                    // then
                    exception.localizedMessage.shouldBe(FAIL_ADD_FOLLOWEE)
                }
            }
        }

        describe("Follow 엔티티 생성시") {
            context("자신이 자신을 팔로우 하는 경우") {
                it("예외를 던진다") {
                    // given
                    val followerId = 1L
                    val followeeId = 1L

                    // when
                    val exception: IllegalArgumentException = shouldThrow {
                        Follow(followerId = followerId, followeeId = followeeId)
                    }

                    // then
                    exception.localizedMessage.shouldBe(INVALID_FOLLOWER_ID_FOLLOWEE_ID)
                }
            }
        }
    },
)