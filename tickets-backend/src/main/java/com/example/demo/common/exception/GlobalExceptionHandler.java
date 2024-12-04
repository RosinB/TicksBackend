package com.example.demo.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.demo.util.ApiResponse;

//利用 @ControllerAdvice 的特性來處理全局錯誤
@ControllerAdvice
public class GlobalExceptionHandler {

	// 當系統發生 NumberFormatException 或 HttpStatus.BAD_REQUEST 時的解決方法
	@ExceptionHandler(NumberFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ApiResponse<Object>> handleNumberFormatException(NumberFormatException e) {
		ApiResponse<Object> apiResponse = ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "無效的數據格式" + e,null);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);

	}
	
	

	// 當系統發生 RuntimeException
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<Object>> handleRuntimeNumberFormatException(RuntimeException e) {
		ApiResponse<Object> apiResponse = ApiResponse.error(HttpStatus.FORBIDDEN.value(), "執行時期錯誤, " + e,null);
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
	}

	// 當系統發生 Exception 或 HttpStatus.INTERNAL_SERVER_ERROR 時的解決方法
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
		ApiResponse<Object> apiResponse = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "伺服器內部錯誤, " + e,null);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
	}
	

	
    // 處理驗證異常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        
        // 提取驗證失敗的字段和錯誤信息
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ApiResponse<Object> apiResponse = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "驗證失敗",
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}
	
	

