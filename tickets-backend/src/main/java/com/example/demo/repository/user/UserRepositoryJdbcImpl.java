package com.example.demo.repository.user;

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
import com.example.demo.util.Hash;

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
		
		List<User> user=jdbcTemplate.query(findAllSql, new BeanPropertyRowMapper<>(User.class));
		return  user;
	}

	@Override
	public Optional<User> findUserById(Integer userId) {
		String sql="select * from users where userId=?";
		
		try {
			User user =jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class),userId);
			return Optional.of(user);

		} catch (Exception e) {
			logger.info(e.toString());
		}
		return Optional.empty();
	}

	@Override
	public int addUser(User user) {
		
		if(user==null) {
			System.out.println("addUser這裡的user是空值");
		}

		return jdbcTemplate.update(saveSql,user.getUserName() ,user.getUserPwdHash(),
										   user.getSalt()	  ,user.getUserPhone(),
										   user.getUserEmail(),user.getUserIdCard());
		
	}

	@Override
	public int updateUser(User user) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteUser(Integer userId) {
		// TODO Auto-generated method stub
		return 0;
	}

}
