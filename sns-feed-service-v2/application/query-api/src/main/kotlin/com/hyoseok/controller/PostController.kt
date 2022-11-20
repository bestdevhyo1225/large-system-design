package com.hyoseok.controller

import com.hyoseok.post.dto.PostDto
import com.hyoseok.post.service.PostReadService
import com.hyoseok.response.SuccessResponse
import com.hyoseok.usecase.FindPostTimelineUsecase
import com.hyoseok.usecase.FindPostUsecase
import com.hyoseok.util.PageByPosition
import com.hyoseok.util.PageRequestByPosition
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts")
class PostController(
    private val findPostUsecase: FindPostUsecase,
    private val findPostTimelineUsecase: FindPostTimelineUsecase,
    private val postReadService: PostReadService,
) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<SuccessResponse<PostDto>> =
        ResponseEntity.ok(SuccessResponse(data = findPostUsecase.execute(postId = id)))

    @GetMapping("/members/{memberId}")
    fun getPosts(
        @PathVariable
        memberId: Long,
        pageRequestByPosition: PageRequestByPosition,
    ): ResponseEntity<SuccessResponse<PageByPosition<PostDto>>> =
        ResponseEntity.ok(
            SuccessResponse(
                data = postReadService.findPosts(
                    memberId = memberId,
                    pageRequestByPosition = pageRequestByPosition,
                ),
            ),
        )

    @GetMapping("/members/{memberId}/timeline")
    fun getTimeline(
        @PathVariable
        memberId: Long,
        pageRequestByPosition: PageRequestByPosition,
    ): ResponseEntity<SuccessResponse<PageByPosition<PostDto>>> =
        ResponseEntity.ok(
            SuccessResponse(
                data = findPostTimelineUsecase.execute(
                    memberId,
                    pageRequestByPosition = pageRequestByPosition,
                ),
            ),
        )
}
