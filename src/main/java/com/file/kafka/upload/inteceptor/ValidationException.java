package com.file.kafka.upload.inteceptor;

public class ValidationException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public ValidationException(String errorMessage) {
        super(errorMessage);
    }
}