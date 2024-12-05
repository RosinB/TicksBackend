package com.example.demo.repository.user;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.user.User;
import com.example.demo.util.DatabaseUtils;

@Repository
@Qualifier("userJDBC")

public class UserRepositoryJdbcImpl implements UserRepositoryJdbc {

	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final class SQL{
		private static final String FIND_ALL="""
				SELECT user_id, user_name, user_pswd_hash, user_phone, user_email, user_idcard, user_birth_date, user_regdate, user_is_verified FROM users;

			""";
		private static final String FIND_USER_BY_ID="""
				select * from users where userId=?
				""".trim();
		private static final String ADD_USER="""
				insert into 
				users(user_name,	 user_pswd_hash, 	user_phone,	 user_email,	 user_idcard, 	user_birth_date,	 user_is_verified) 
				value(?,?,?,?,?,?,0)

		""".trim();		
		private static final String FIND_USER_ISVERIFIED_BY_USER_NAME="""
				select user_is_verified from users where user_name=?
				""".trim();	
		private static final String FIND_USER_EMAIL_BY_USER_NAME="""
				select user_email 
				from users
				where user_name=?
				""".trim();
		private static final String UPDATE_USER_ISVERIFIED="""
				update  users
				set 	user_is_verified=true
				where   user_name=?
				""".trim();
		
	}
	
	
	
	
	private static final RowMapper<User> userMapper=new BeanPropertyRowMapper<>(User.class);
	
	
	@Override
	public List<User> findAll() {
		

		return DatabaseUtils.executeQuery(
				"findAll", 
				()->jdbcTemplate.query(SQL.FIND_ALL, (rs, rowNum) -> {
					    User user = new User();
					    user.setUserId(rs.getInt("user_id"));
					    user.setUserName(rs.getString("user_name"));
					    user.setUserPwdHash(rs.getString("user_pswd_hash"));
					    user.setUserPhone(rs.getString("user_phone"));
					    user.setUserEmail(rs.getString("user_email"));
					    user.setUserIdCard(rs.getString("user_idcard")); 
					    user.setUserBirthDate(rs.getObject("user_birth_date", LocalDate.class));
					    user.setUserRegdate(rs.getTimestamp("user_regdate"));
					    user.setUserIsVerified(rs.getBoolean("user_is_verified"));
					    return user;
				}), 
				"查詢所有人失敗"
		);

	}

	@Override
	public Optional<User> findUserById(Integer userId) {
		
		
			return Optional.ofNullable(DatabaseUtils.executeQuery(
													"findUserById", 
													()->jdbcTemplate.queryForObject(SQL.FIND_USER_BY_ID,userMapper,userId), 
													"使用者查不到"));
		
	}

	@Override
	public int addUser(User user) {
	
		return DatabaseUtils.executeQuery(
				"addUser",
				()->jdbcTemplate.update(
						SQL.ADD_USER,
						user.getUserName()   ,user.getUserPwdHash(),
						user.getUserPhone()  ,user.getUserEmail(),
						user.getUserIdCard() ,user.getUserBirthDate()), 
				"使用者新增錯誤");
		
		
		
	}

	@Override
	public Boolean findUserIsVerifiedByUserName(String userName) {
	
		return DatabaseUtils.executeQuery(
				"findUserIsVerifiedByUserName", 
				()->jdbcTemplate.queryForObject(SQL.FIND_USER_ISVERIFIED_BY_USER_NAME,Boolean.class,userName), 
				"使用者認證查詢不到");
		
		
				
		
	}

	@Override
	public String findUserEmailByUserName(String userName) {

		return DatabaseUtils.executeQuery(
				"findUserEmailByUserName", 
				()->jdbcTemplate.queryForObject(SQL.FIND_USER_EMAIL_BY_USER_NAME,String.class,userName), 
				"查不到使用者信箱");

	}

	
	@Override
	public void updateUserIsVerified(String userName) {

		
		DatabaseUtils.executeQuery(
				"updateUserIsVerified", 
				()->jdbcTemplate.update(SQL.UPDATE_USER_ISVERIFIED,userName), 
				"使用者驗證更新失敗");
		
	}


}
