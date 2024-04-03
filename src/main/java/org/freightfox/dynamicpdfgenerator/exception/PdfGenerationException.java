package org.freightfox.dynamicpdfgenerator.exception;

import lombok.Data;

@Data
public class PdfGenerationException extends RuntimeException{
    private String message;
    private String errorCode;
    private String errorDescription;
    public PdfGenerationException(String message, String errorCode, String errorDescription) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }
}
