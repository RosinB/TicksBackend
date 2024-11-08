package com.example.demo.repository.user;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.user.User;

@Repository
public class UserRepositoryJdbcImpl implements UserRepositoryJdbc {

	private static final Logger logger = LoggerFactory.getLogger(UserRepositoryJdbc.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<User> findAll() {
		String sql = "select *  from users";
		List<User> user=jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
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
	
		
		
		return 0;
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
