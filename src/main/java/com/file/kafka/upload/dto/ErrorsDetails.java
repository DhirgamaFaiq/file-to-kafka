package com.file.kafka.upload.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorsDetails {
	private String title;
	private String details;
	private String code;
}
