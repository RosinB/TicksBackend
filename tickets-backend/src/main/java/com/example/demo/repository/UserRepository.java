package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
	
	//檢查是否有userNmae
	Boolean existsByUserName(String userName);
	
	

	
	//用userName查user資料
	@Query("select u from User u where u.userName = :userName")
	Optional<User> findUserByUserName(@Param("userName")String userName);
	
	//透過userName查hashpassword
	@Query("select u.userPwdHash from User u where u.userName=:userName")
	String findHashPasswordByUserName(@Param("userName") String userName);
	
	//透過userName查使用者userID	
	@Query("select u.userId from User u where u.userName=:userName")
	Integer findIdByUserName(@Param("userName") String userName);
	
	
	
	@Modifying
	@Query("update User u set u.userPhone =:userPhone , u.userEmail = :userEmail , u.userBirthDate = :userBirthDate where u.userName= :userName")
	Integer updateUser(@Param("userPhone") String userPhone,
					   @Param("userEmail") String userEmail,
					   @Param("userBirthDate") LocalDate userBirthDate,
					   @Param("userName") String userName);
	
	
	
}
