package com.stockpulse.stockpulseAPI.post.service;

import com.stockpulse.stockpulseAPI.domain.post.dto.PostResponseDTO;
import com.stockpulse.stockpulseAPI.domain.post.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@Slf4j
public class PostServiceTest {

    @Autowired private PostService postService;

    @Test
    @DisplayName("게시글 목록 조회")
    void getPostList() {
        List<PostResponseDTO.SummaryDTO> latest = postService.getPostList(0, 5, "latest");

        log.info("latest = {}", latest);
    }
}
