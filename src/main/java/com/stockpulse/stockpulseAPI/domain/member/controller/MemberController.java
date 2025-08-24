package com.stockpulse.stockpulseAPI.domain.member.controller;

import com.stockpulse.stockpulseAPI.domain.member.dto.MemberRequestDTO;
import com.stockpulse.stockpulseAPI.domain.member.service.MemberService;
import com.stockpulse.stockpulseAPI.domain.news.dto.NewsResponseDTO;
import com.stockpulse.stockpulseAPI.domain.post.dto.PostResponseDTO;
import com.stockpulse.stockpulseAPI.global.apiPayload.ApiResponse;
import com.stockpulse.stockpulseAPI.global.security.handler.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Tag(name = "Member", description = "사용자 관련 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "사용자 닉네임 조회")
    @GetMapping("/nickname")
    public ResponseEntity<ApiResponse<String>> getNickname(
            @AuthUser Long userId
    ) {
        String response = memberService.getNickname(userId);
        return ResponseEntity.ok(
                ApiResponse.onSuccess(response)
        );
    }

    @Operation(
            summary = "사용자 닉네임 변경",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "닉네임 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberRequestDTO.UpdateDTO.class)
                            , examples = @ExampleObject(value = """
                            {
                                "nickname": "주식똑똑이"
                            }
                            """
                    )
                    )
            )
    )
    @PatchMapping("/nickname")
    public ResponseEntity<ApiResponse<String>> updateNickname(
            @AuthUser Long userId,
            @RequestBody MemberRequestDTO.UpdateDTO request
    ) {
        memberService.updateNickname(userId, request);
        return ResponseEntity.ok(
                ApiResponse.onSuccess("success")
        );
    }

    @GetMapping("/news/scrap")
    public ResponseEntity<ApiResponse<List<NewsResponseDTO.NewsOverviewDTO>>> getScrapNews(@AuthUser Long userId) {
        List<NewsResponseDTO.NewsOverviewDTO> response = memberService.getScrapNews(userId);
        return ResponseEntity.ok(
                ApiResponse.onSuccess(response)
        );
    }

    @GetMapping("/post/publish")
    public ResponseEntity<ApiResponse<List<PostResponseDTO.SummaryDTO>>> getPublishedPosts(@AuthUser Long userId) {
        List<PostResponseDTO.SummaryDTO> response = memberService.getPublishedPosts(userId);

        return ResponseEntity.ok(
                ApiResponse.onSuccess(response)
        );
    }

    @GetMapping("/post/comment")
    public ResponseEntity<ApiResponse<List<PostResponseDTO.SummaryDTO>>> getCommentedPosts(@AuthUser Long userId) {
        List<PostResponseDTO.SummaryDTO> response = memberService.getCommentedPosts(userId);
        return ResponseEntity.ok(
                ApiResponse.onSuccess(response)
        );
    }
}






