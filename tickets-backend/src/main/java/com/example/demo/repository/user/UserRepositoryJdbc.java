package com.example.demo.repository.user;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.entity.user.*;



public interface UserRepositoryJdbc {
	//查全部
	List<User> findAll();
	//查單個
	Optional<User> findUserById(Integer userId);
	
	//新增一個
	int addUser(User user);

	//更新一個
	int updateUser(User user);
	
	//刪除一個
	int deleteUser(Integer userId);
	
	
	
}
