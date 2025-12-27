package com.inninglog.inninglog.domain.contentImage.service;

import com.inninglog.inninglog.domain.contentImage.domain.ContentImage;
import com.inninglog.inninglog.domain.contentImage.dto.req.ImageRemainUpdateReqDto;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostImageUpdateService {

    private final PostImageDeleteService postImageDeleteService;

    @Transactional
    public void updateImages(
            List<ImageRemainUpdateReqDto> remainImages,
            List<ContentImage> existingImages
    ) {
        // 2) 유지할 이미지 map 구성 (id → sequence)
        Map<Long, Integer> remainSeqMap = remainImages.stream()
                .collect(Collectors.toMap(
                        ImageRemainUpdateReqDto::remainImageId,
                        ImageRemainUpdateReqDto::sequence
                ));

        // 3) 기존 이미지 중 삭제 대상 제거
        for (ContentImage img : existingImages) {
            if (!remainSeqMap.containsKey(img.getId())) {
                postImageDeleteService.delete(img);
            }
        }

        // 4) 유지되는 이미지의 sequence 업데이트
        for (ContentImage img : existingImages) {
            if (remainSeqMap.containsKey(img.getId())) {
                img.updateSequence(remainSeqMap.get(img.getId()));
            }
        }
    }
}
