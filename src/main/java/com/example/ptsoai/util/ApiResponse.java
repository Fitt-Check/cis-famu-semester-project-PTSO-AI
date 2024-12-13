package com.example.ptsoai.util;

public record ApiResponse<T>(boolean success, String message, T data, Object error) {
    public ApiResponse(boolean success, String message) {
        this(success, message, null, null); // Call canonical constructor
    }
}
