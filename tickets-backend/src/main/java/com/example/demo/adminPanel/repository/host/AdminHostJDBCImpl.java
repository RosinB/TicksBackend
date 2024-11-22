package com.example.demo.adminPanel.repository.host;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.adminPanel.dto.event.EventDetailDto;
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

	
	@Override
	public void addPic(EventDetailDto dto, Integer eventId) {
		String sql="""
				insert into pic(event_id,		pic_event_ticket,	
								pic_event_list,	pic_event_section)
							values(?,?,?,?)
				""".trim();
		try {
			jdbcTemplate.update(sql,eventId,dto.getPicEventTicket(),dto.getPicEventList(),dto.getPicEventSection());
			
		} catch (Exception e) {
			logger.info("添加addPic有錯誤，這個在AdminHostJDBC。"+e.getMessage());
			throw new RuntimeException("添加addPic有錯誤，這個在AdminHostJDBC。"+e.getMessage());	
		}
		
		
	}


	
	@Override
	public void updatePic(EventDetailDto dto, Integer eventId) {
		String sql="""
					update pic
					set    
						   pic_event_ticket 	= ?,
						   pic_event_list		= ?,
					       pic_event_section	= ?
					where  event_id	=?
				""".trim();
		try {
			int row=jdbcTemplate.update(sql,
										dto.getPicEventTicket(),dto.getPicEventList(),dto.getPicEventSection(),
										eventId);
			if(row<1) {
				logger.info("updatePic更新為0條");
				throw new RuntimeException("updatePic更新為0條");
			}
			
		} catch (Exception e) {
			logger.info("updatePic更新失敗",e.getMessage());
			throw new RuntimeException("updatePic更新失敗"+e.getMessage());
		}
		
		
	}
	

	
	
	
}
