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
import org.springframework.web.servlet.HandlerMapping;

import com.example.demo.common.exception.User.UserNotFoundException;
import com.example.demo.util.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

//利用 @ControllerAdvice 的特性來處理全局錯誤
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	// 當系統發生 NumberFormatException 或 HttpStatus.BAD_REQUEST 時的解決方法
	@ExceptionHandler(NumberFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ApiResponse<Object>> handleNumberFormatException(NumberFormatException e) {
		ApiResponse<Object> apiResponse = ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "無效的數據格式" + e,null);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);

	}
	
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
	        RuntimeException e,
	        HttpServletRequest request) {
	    
	    // 獲取當前請求的Controller和方法名稱
	    String handlerMethod = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE) != null ?
	            request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE).toString() : "未知方法";
	            
	    // 記錄詳細的錯誤資訊
	    log.error("""
	            運行時異常:
	            控制器: {}
	            請求方法: {}
	            請求路徑: {}
	            請求參數: {}
	            異常類型: {}
	            錯誤訊息: {}
	            堆疊追蹤: 
	            """,
	            handlerMethod,
	            request.getMethod(),
	            request.getRequestURI(),
	            request.getParameterMap(),
	            e.getClass().getSimpleName(),
	            e.getMessage(),
	            e  // 這會印出完整堆疊追蹤
	    );

	    // 根據不同的異常類型返回不同的狀態碼
	    if (e instanceof UserNotFoundException) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(ApiResponse.error(404, e.getMessage(), null));
	    }

	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(ApiResponse.error(500, "系統錯誤：" + e.getMessage(), null));
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
	
	

