package com.hyoseok.feed.entity

class FeedCache {
    companion object {
        const val ZSET_FEED_MAX_LIMIT = -801L

        fun getMemberIdFeedsKey(id: Long) = "member:$id:feeds"
        fun getMemberIdFeedsKeyAndExpireTime(id: Long) =
            Pair(first = getMemberIdFeedsKey(id = id), second = 60 * 60 * 24L) // 24시간
    }
}
