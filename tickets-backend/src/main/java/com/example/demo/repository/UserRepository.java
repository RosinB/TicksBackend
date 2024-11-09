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

	@Query("SELECT u FROM User u WHERE u.userName = :userName OR u.userPhone = :userPhone OR u.userEmail = :userEmail OR u.userIdCard = :userIdCard")
	List<User> findConflictingUsers(@Param("userName") String userName,
	                                 @Param("userPhone") String userPhone,
	                                 @Param("userEmail") String userEmail,
	                                 @Param("userIdCard") String userIdCard);
	
	
}
