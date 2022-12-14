package com.hyoseok.feed.repository

import java.util.concurrent.TimeUnit

interface FeedCacheReadRepository {
    fun getExpire(key: String, timeUnit: TimeUnit): Long
    fun <T> zrevrange(key: String, start: Long, end: Long, clazz: Class<T>): List<T>
    fun <T> zrevrangebyscore(key: String, min: Double, max: Double, start: Long, end: Long, clazz: Class<T>): List<T>
}
