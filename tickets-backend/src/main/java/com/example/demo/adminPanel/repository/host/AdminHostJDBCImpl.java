package com.example.demo.adminPanel.repository.host;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.adminPanel.dto.event.EventDetailDto;

import com.example.demo.util.DatabaseUtils;
@Repository
public class AdminHostJDBCImpl implements AdminHostJDBC{
	
	private static final class SQL{
		static final String FIND_HOSTID_BY_HOSTNAME="""
				select host_id from host where host_name = ?		
			""".trim();
		static final String ADD_PIC="""
				insert into pic(event_id,		pic_event_ticket,	
				pic_event_list,	pic_event_section)
			values(?,?,?,?)
""".trim();
		static final String UPDATE_PIC="""
				update pic
				set    
					   pic_event_ticket 	= ?,
					   pic_event_list		= ?,
				       pic_event_section	= ?
				where  event_id	=?
			""".trim();
	}
	
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public int findHostIdByHostName(String hostName) {
		
		return DatabaseUtils.executeQuery(
							"findHostIdByHostName", 
							()->jdbcTemplate.queryForObject(SQL.FIND_HOSTID_BY_HOSTNAME, Integer.class,hostName), 
							String.format("找尋活動主辦Id失敗->HostName:", hostName));
		
	}
	@Override
	public void addPic(EventDetailDto dto, Integer eventId) {

		DatabaseUtils.executeUpdate(
					 "addPic", 
					 ()->jdbcTemplate.update(SQL.ADD_PIC,eventId,dto.getPicEventTicket(),dto.getPicEventList(),dto.getPicEventSection()), 
					 String.format("添加照片失敗->eventID:%d", eventId));	
	}
	@Override
	public void updatePic(EventDetailDto dto, Integer eventId) {
	
		DatabaseUtils.executeUpdate(
					"updatePic",
					()->jdbcTemplate.update(SQL.UPDATE_PIC,
							dto.getPicEventTicket(),dto.getPicEventList(),dto.getPicEventSection(),
							eventId), 
					String.format("更新照片失敗->eventID:%d",eventId));	
	}

}
