package com.file.kafka.upload.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseResponse {
    private String message;
    private ErrorsDetails errorsDetails;
}