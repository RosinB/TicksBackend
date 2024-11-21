package com.example.demo.adminPanel.repository.host;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.adminPanel.repository.event.AdminEventJDBC;
import com.example.demo.adminPanel.repository.event.AdminEventJDBCImpl;
@Repository
public class AdminHostJDBCImpl implements AdminHostJDBC{
	private  static final Logger logger = LoggerFactory.getLogger(AdminEventJDBCImpl.class);
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public int findHostIdByHostName(String hostName) {
		String sql="""
					select host_id from host where host_name = ?		
				""".trim();
		try {
			
			return jdbcTemplate.queryForObject(sql, Integer.class,hostName); 
			
		} catch (Exception e) {
			logger.info("findHostIdByHostName 主辦名字對應不到主辦ID"+e.getMessage());
			throw new RuntimeException("主辦名字對應不到主辦ID"+e.getMessage());
		}
		
	
	}
	

}
