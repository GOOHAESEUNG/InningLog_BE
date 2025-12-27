package com.inninglog.inninglog.domain.post.service;

import com.inninglog.inninglog.domain.contentImage.domain.ContentImage;
import com.inninglog.inninglog.domain.contentImage.dto.req.ImageRemainUpdateReqDto;
import com.inninglog.inninglog.domain.contentImage.dto.req.ImageUploadReqDto;
import com.inninglog.inninglog.domain.contentImage.service.PostImageDeleteService;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.post.domain.Post;
import com.inninglog.inninglog.domain.post.dto.req.PostUpdateReqDto;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostUpdateService {
    private final PostImageDeleteService postImageDeleteService;

    //게시글 수정
    @Transactional
    public void updatePostFromDto(Post post, PostUpdateReqDto dto) {
        post.update(dto);
    }
}
