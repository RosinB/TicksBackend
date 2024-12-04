package com.example.demo.common.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtInterceptor implements HandlerInterceptor {
	private final static Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);

    @Value("${jwt.public-urls}")
	private List<String> publicUrls;
	private final JwtUtil jwtUtil;

	public JwtInterceptor(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String requestURI = request.getRequestURI();

		if (isPublicUrl(requestURI)) {
			return true;
		}

		String token = request.getHeader("Authorization");

		if (token == null || !token.startsWith("Bearer")) {
			sendErrorResponse(response, "Missing or invalid token");
			return false;
		}

		token = token.substring(7);
		if (!jwtUtil.isTokenValid(token)) {
			sendErrorResponse(response, "Token is invalid or expired");
			return false;
		}

//		logger.info("Access granted for URI: {}", requestURI);
		return true;
	}

	private boolean isPublicUrl(String requestURI) {
		return publicUrls.stream().anyMatch(pattern -> new AntPathMatcher().match(pattern, requestURI));
	}

	private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("error", message);
		errorResponse.put("timestamp", new Date().toString());

		response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
	}
}
