package com.stockpulse.stockpulseAPI.domain.post.controller;

import com.stockpulse.stockpulseAPI.domain.news.dto.NewsResponseDTO;
import com.stockpulse.stockpulseAPI.domain.post.dto.PostRequestDto;
import com.stockpulse.stockpulseAPI.domain.post.dto.PostResponseDto;
import com.stockpulse.stockpulseAPI.domain.post.service.PostService;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
import com.stockpulse.stockpulseAPI.global.apiPayload.ApiResponse;
import com.stockpulse.stockpulseAPI.global.security.handler.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
@Tag(name = "Post", description = "게시글 관련 API")
public class PostController {

    private final PostService postService;


    // TODO : 게시글 목록 조회
    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<PostResponseDto.PostSummaryDto>>> list(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String sort) {
        List<PostResponseDto.PostSummaryDto> posts = postService.getPostList(page, size, sort);
        return ResponseEntity
                .ok()
                .body(ApiResponse.onSuccess(posts));
    }

    // 게시글 작성 뉴스 정보 조회
    @Operation(summary = "게시글 작성을 위한 뉴스 정보 조회", description = "게시글 작성 페이지 진입 시, 선택한 뉴스의 정보를 미리 채우기 위해 호출하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "뉴스 정보 조회 성공", content = @Content(schema = @Schema(implementation = NewsResponseDTO.PostPrefillDTO.class))),
    })
    @GetMapping("/write")
    public ApiResponse<NewsResponseDTO.PostPrefillDTO> prefill(
            @RequestParam("newsId") Long newsId
    ) {
        NewsResponseDTO.PostPrefillDTO response = postService.getPrefillNewsInfo(newsId);
        return ApiResponse.onSuccess(response);
    }

    // 주식 검색
    @GetMapping("/write/stock")
    @Operation(
            summary = "주식 검색"
    )
    public ApiResponse<StockResponseDTO.StockSummaryDTO> getStock(@RequestBody String keyword) {

        // TODO : 주식 종목 검색 모듈 import 필요

        return ApiResponse.onSuccess(StockResponseDTO.StockSummaryDTO.builder()
                .symbol("005930")
                .name("삼성전자")
                .imageUrl("https://example.com")
                .build());
    }

    // 게시글 등록
    @Operation(summary = "게시글 등록", description = "게시글을 등록합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "등록할 게시글 내용", required = true, content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PostRequestDto.class),
                    examples = @ExampleObject(value = "{\"newsId\": 13, \"title\": \"새로운 게시글 제목\", \"content\": \"게시글 내용입니다...\", \"stockId\": \"13\", \"requireVote\": true}")
            )))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "댓글 성공적으로 등록됨",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostResponseDto.CommentResponseDto.class),
                            examples = @ExampleObject(value = "{\"isSuccess\":true,\"code\":\"201\",\"message\":\"Created\",\"result\":{\"postId\":101}}"))),
    })
    @PostMapping("/write")
    public ResponseEntity<ApiResponse<Map<String,Long>>> write(
            @AuthUser Long userId,
            @RequestBody PostRequestDto dto
    ) {
        Long postId = postService.createPost(userId, dto);

        URI location = URI.create("/api/post/" + postId);

        return ResponseEntity
                .created(location) // ← HTTP 201 + Location 헤더 자동 추가
                .body(ApiResponse.onSuccess(Map.of("postId", postId))); // ← 바디는 통일된 구조
    }

    // 댓글 등록
    @PostMapping("/comment/{postId}")
    @Operation(summary = "댓글 등록", description = "댓글을 등록합니다.")
    public ResponseEntity<ApiResponse<PostResponseDto.CommentResponseDto>> comment(
            @AuthUser Long userId,
            @PathVariable Long postId,
            @RequestBody String content
    ) {
        PostResponseDto.CommentResponseDto response = postService.createComment(userId, postId, content);

        return ResponseEntity
                .created(URI.create("/api/post/" + postId))
                .body(ApiResponse.onSuccess(response));
    }


    // TODO : 게시글 상세 조회
    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "게시글 단건을 상세 조회합니다.")
    public ResponseEntity<ApiResponse<PostResponseDto.PostDetailDto>> detail(@AuthUser Long userId, @PathVariable Long postId) {
        PostResponseDto.PostDetailDto response = postService.getPostDetail(userId, postId);

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(response));
    }


    // TODO : 투표 참여
    @PostMapping("/vote/{postId}")
    @Operation(
            summary = "투표 참여",
            description = "사용자들이 게시글로 등록된 주식의 등락에 대한 투표를 참여합니다..",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "투표 타입 (0: 매수, 1: 매도, 2: 상승)",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(type = "integer", allowableValues = {"0", "1", "2"}),
                        examples = @ExampleObject(value = "{\"voteType\":0}")
                )
            ),
            parameters = {
                @io.swagger.v3.oas.annotations.Parameter(
                        name = "postId",
                        description = "게시글 ID",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(type = "integer", format = "int64")
                )
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "투표 참여 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                        schema = @Schema(implementation = PostResponseDto.VoteResponseDTO.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "isSuccess": true,
                                                  "code": "200",
                                                  "message": "OK",
                                                  "result": {
                                                    "postId": 101,
                                                    "voteType": 0,
                                                    "voteSummary": {
                                                      "total": 134,
                                                      "buy": 72,
                                                      "sell": 31,
                                                      "hold": 31
                                                    },
                                                    "userVoted": true
                                                  }
                                                }
                                                """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<PostResponseDto.VoteResponseDTO>> vote(
            @AuthUser long userId,
            @PathVariable long postId,
            @RequestBody PostRequestDto.VoteParticipationDTO voteParticipationDTO) {
        PostResponseDto.VoteResponseDTO response = postService.vote(userId, postId, voteParticipationDTO);
        return ResponseEntity
                .created(URI.create("/api/post/" + postId))
                .body(ApiResponse.onSuccess(response));
    }
}
