package com.codecluster.auth.exception;

import java.time.OffsetDateTime;

public class ErrorResponse {

    private OffsetDateTime timestamp;
    private int status;
    private String message;

    public ErrorResponse() {
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}