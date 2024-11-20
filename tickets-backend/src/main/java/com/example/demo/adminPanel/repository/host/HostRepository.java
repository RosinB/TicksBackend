package com.example.demo.adminPanel.repository.host;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.adminPanel.dto.HostDto;
import com.example.demo.model.entity.host.Host;

@Repository
public interface HostRepository  extends JpaRepository<Host, Integer>{

	
	@Query("select new com.example.demo.adminPanel.dto.HostDto(h.hostId, h.hostName, h.hostContact,h.hostDescription) from Host h")
	List<HostDto> findAllHosts();
	
	
}
