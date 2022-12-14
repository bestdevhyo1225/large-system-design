package com.hyoseok.repository

import kotlin.math.abs
import kotlin.math.ln

abstract class AbstractCacheRepository {

    /**
     * [ remainingExpiryTimeMS ]
     * - 남은 만료 시간
     * [ delta ]
     * - 캐시를 다시 계산하기 위한 시간 범위 (단위: MS)
     * [ beta ]
     * - 가중치 (기본 값으로 1.0을 사용한다.)
     * - ex) beta < 1.0 => 조금 더 소극적으로 재 계산한다.
     * - ex) beta > 1.0 => 조금 더 적극적으로 재 계산한다.
     * */
    protected fun isRefreshKey(remainingExpiryTimeMS: Long, delta: Long = 3_000L, beta: Float = 1.0f): Boolean {
        return remainingExpiryTimeMS >= 0 && abs(delta * beta * ln(Math.random())) >= remainingExpiryTimeMS
    }
}
