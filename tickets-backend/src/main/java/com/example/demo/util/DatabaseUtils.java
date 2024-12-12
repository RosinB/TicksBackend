package com.example.demo.util;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

public class DatabaseUtils {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);

	public static <T> T executeQuery(String operation, Supplier<T> query, String errorMessage) {

		try {
			return query.get();
		} catch (EmptyResultDataAccessException e) {
			logger.info(errorMessage, e.getMessage());
			throw new RuntimeException(errorMessage + e.getMessage());

		} catch (DataAccessException e) {
			logger.error(errorMessage, e.getMessage());
			throw new RuntimeException(errorMessage + e.getMessage());

		} catch (Exception e) {
			logger.error("Operation '{}' failed: {}", operation, errorMessage, e);
			throw new RuntimeException(errorMessage + e.getMessage());
		}

	}

	public static void executeUpdate(String operation, Supplier<Integer> update, String errorMessage) {
	    try {
	        int result = update.get();
	        if (result < 1) {
	            logger.warn("{}更新失敗，影響筆數: {}", operation, result);
	            throw new RuntimeException("更新失敗(筆數為0): " + errorMessage);
	        }
	        // 成功時也記錄日誌
//	        logger.info("{}更新成功，影響筆數: {}", operation, result);
	        
	    } catch (EmptyResultDataAccessException e) {
	        logger.info("{}：{}", errorMessage, e.getMessage());
	        throw new RuntimeException(errorMessage + e.getMessage());

	    } catch (DataAccessException e) {
	        logger.error("{}：{}", errorMessage, e.getMessage());
	        throw new RuntimeException(errorMessage + e.getMessage());

	    } catch (Exception e) {
	        logger.error("Operation '{}' failed: {}", operation, errorMessage, e);
	        throw new RuntimeException(errorMessage + e.getMessage());
	    }
	}
}
