package com.hyoseok.web

import com.hyoseok.service.dto.MemberCreateResultDto
import com.hyoseok.service.member.MemberService
import com.hyoseok.web.request.MemberCreateRequest
import com.hyoseok.web.response.SuccessResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberService: MemberService,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody
        request: MemberCreateRequest,
    ): ResponseEntity<SuccessResponse<MemberCreateResultDto>> {
        val memberCreateResultDto: MemberCreateResultDto = memberService.create(dto = request.toServiceDto())
        return ResponseEntity.created(URI.create("/api/v1/members/${memberCreateResultDto.memberId}"))
            .body(SuccessResponse(data = memberCreateResultDto))
    }
}
