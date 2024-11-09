package com.example.demo.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.user.User;

@Repository
@Qualifier("userJPA")
public interface UserRepository  extends JpaRepository<User, Integer>{

    boolean existsByuserName(String userName);

	
	
	
}
