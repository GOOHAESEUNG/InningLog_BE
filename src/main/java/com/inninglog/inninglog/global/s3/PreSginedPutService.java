package com.inninglog.inninglog.global.s3;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreSginedPutService {

    private final S3Uploader  s3Uploader;

    public String journalPutPreUrl(Long memebrId, String fileName, String contentType){

        if (fileName.contains("..") || fileName.contains("/")) {
            throw new CustomException(ErrorCode.INVALID_FILE_NAME);
        }

        String url = s3Uploader.generatePresignedUrl(
                "journal/" + memebrId +"/" +  fileName, contentType);

        return url;
    }

    public String seatViewPutPreUrl(Long memebrId, String fileName, String contentType){

        if (fileName.contains("..") || fileName.contains("/")) {
            throw new CustomException(ErrorCode.INVALID_FILE_NAME);
        }

        String url = s3Uploader.generatePresignedUrl(
                "seatView/" + memebrId +"/" +  fileName, contentType);

        return url;
    }

    public String postPutPreUrl(Long memebrId, String fileName, String contentType){

        if (fileName.contains("..") || fileName.contains("/")) {
            throw new CustomException(ErrorCode.INVALID_FILE_NAME);
        }

        String url = s3Uploader.generatePresignedUrl(
                "post/" + memebrId +"/" +  fileName, contentType);

        return url;
    }

}
