package com.example.demo.adminPanel.repository.host;

import java.util.List;


import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.adminPanel.dto.event.EventDetailDto;
import com.example.demo.adminPanel.dto.host.HostDto;
import com.example.demo.util.DatabaseUtils;

import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class AdminHostJDBCImpl implements AdminHostJDBC {

	
	private static final class SQL {
		static final String FIND_ALL_HOST="""
				select  
					   host_id   as hostId,
					   host_name as hostName,
					   host_contact as hostContact,
					   host_description as hostDescription
				from host
				
				""".trim();
		static final String FIND_HOSTID_BY_HOSTNAME = """
					select host_id from host where host_name = ?
				""".trim();
		static final String ADD_PIC = """
								insert into pic(event_id,		pic_event_ticket,
								pic_event_list,	pic_event_section)
							values(?,?,?,?)
				""".trim();
		static final String UPDATE_PIC = """
					update pic
					set
						   pic_event_ticket 	= ?,
						   pic_event_list		= ?,
					       pic_event_section	= ?
					where  event_id	=?
				""".trim();
		static final String ADD_HOST="""
					insert into host(host_name,host_contact,host_description)
					value(?,?,?)
		
				
				""".trim();
		static final String UPDATE_HOST="""
				update host
				set   host_name=?,
					  host_contact=?,
				  	  host_description=?
				where host_id=?
				
				
				
				""".trim();
	}
	private final static RowMapper<HostDto> hostMapper =new BeanPropertyRowMapper<>(HostDto.class);

	private final JdbcTemplate jdbcTemplate;

	
	@Override
	public List<HostDto> findAllHost() {

		return DatabaseUtils.executeQuery(
				"findAllHost",
				()->jdbcTemplate.query(SQL.FIND_ALL_HOST, hostMapper), 
				"查詢全部主辦錯誤");
	}

	@Override
	public int findHostIdByHostName(String hostName) {

		return DatabaseUtils.executeQuery("findHostIdByHostName",
				() -> jdbcTemplate.queryForObject(SQL.FIND_HOSTID_BY_HOSTNAME, Integer.class, hostName),
				String.format("找尋活動主辦Id失敗->HostName:", hostName));

	}

	@Override
	public void addPic(EventDetailDto dto, Integer eventId) {

		DatabaseUtils
				.executeUpdate(
						"addPic", () -> jdbcTemplate.update(SQL.ADD_PIC, eventId, dto.getPicEventTicket(),
								dto.getPicEventList(), dto.getPicEventSection()),
						String.format("添加照片失敗->eventID:%d", eventId));
	}

	@Override
	public void updatePic(EventDetailDto dto, Integer eventId) {

		DatabaseUtils
				.executeUpdate(
						"updatePic", () -> jdbcTemplate.update(SQL.UPDATE_PIC, dto.getPicEventTicket(),
								dto.getPicEventList(), dto.getPicEventSection(), eventId),
						String.format("更新照片失敗->eventID:%d", eventId));
	}

	@Override
	public void addHost(HostDto data) {

		DatabaseUtils.executeUpdate(
					"addHost", 
				
					()->jdbcTemplate.update(SQL.ADD_HOST,data.getHostName(),data.getHostContact(),data.getHostDescription()), 
					"主辦新增錯誤");
		
	}

	@Override
	public void updateHost(HostDto data) {

		DatabaseUtils.executeUpdate("updataHost",
					()->jdbcTemplate.update(SQL.UPDATE_HOST,data.getHostName(),data.getHostContact(),data.getHostDescription(),data.getHostId())
					, "主辦更新錯誤");
		
		
	}
	
	
	
	

}
