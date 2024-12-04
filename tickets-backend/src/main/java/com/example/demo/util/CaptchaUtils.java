package com.example.demo.util;

import java.util.Random;
import java.util.stream.Collectors;

public class CaptchaUtils {
	private CaptchaUtils() {
	} // 防止實例化

	public static String generateNumericCaptcha(int length) {
		return new Random().ints(length, 0, 10).mapToObj(String::valueOf).collect(Collectors.joining());
	}

	public static String generateAlphanumericCaptcha(int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}

		return sb.toString();
	}
}
