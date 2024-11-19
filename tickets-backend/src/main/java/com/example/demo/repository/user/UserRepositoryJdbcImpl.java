package com.example.demo.repository.user;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.user.User;

@Repository
@Qualifier("userJDBC")
@PropertySource("classpath:sql.properties") // 自動到 src/main/resources 找到 sql.properties

public class UserRepositoryJdbcImpl implements UserRepositoryJdbc {

	private static final Logger logger = LoggerFactory.getLogger(UserRepositoryJdbc.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Value("${User.sql.findAll}") // ${} SpringEL 語法
	private String findAllSql;
	
	@Value("${User.sql.save}") // ${} SpringEL 語法
	private String saveSql;
	
	
	@Override
	public List<User> findAll() {
			String sql="""
						SELECT user_id, user_name, user_pswd_hash, user_phone, user_email, user_idcard, user_birth_date, user_regdate, user_is_verified FROM users;

					""";
			List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> {
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
			});
			return users;
	}

	@Override
	public Optional<User> findUserById(Integer userId) {
		String sql="select * from users where userId=?";
		
		try {
			User user =jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class),userId);
			return Optional.of(user);

		} catch (Exception e) {
			throw new  RuntimeException("使用者查不到");
		}
	}

	@Override
	public int addUser(User user) {
		
		if(user==null) {
			System.out.println("addUser這裡的user是空值");
		}
		
		return jdbcTemplate.update(saveSql,user.getUserName() ,user.getUserPwdHash(),
										   user.getUserPhone(),user.getUserEmail()
										   ,user.getUserIdCard(),user.getUserBirthDate());
		
	}

	@Override
	public Boolean findUserIsVerifiedByUserName(String userName) {
		String sql="""
				select user_is_verified from users where user_name=?
				""".trim();		
		
		return jdbcTemplate.queryForObject(sql, Boolean.class,userName);
		
				
		
	}

	

}
