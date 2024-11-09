package com.example.demo.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.user.User;

@Repository
@Qualifier("userJPA")
public interface UserRepository  extends JpaRepository<User, Integer>{

	//檢查user資料是否和資料庫重複
	@Query("SELECT u FROM User u WHERE u.userName = :userName OR u.userPhone = :userPhone OR u.userEmail = :userEmail OR u.userIdCard = :userIdCard")
	List<User> findDuplicatesUsers(@Param("userName") String userName,
	                                 @Param("userPhone") String userPhone,
	                                 @Param("userEmail") String userEmail,
	                                 @Param("userIdCard") String userIdCard);
	
	
	Boolean existsByUserName(String userName);
	
	
	@Query("select u.salt from User u where u.userName=:userName")
	String findSaltByUserName(@Param("userName") String userName);
	
	@Query("select u.userPwdHash from User u where u.userName=:userName")
	String findHashPasswordByUserName(@Param("userName") String userName);
}
