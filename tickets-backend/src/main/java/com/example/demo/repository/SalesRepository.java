package com.example.demo.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.sales.SalesDto;
import com.example.demo.model.entity.sales.Sales;


@Qualifier("SalesJPA")
@Repository
public interface SalesRepository  extends JpaRepository<Sales, Integer>{

	
		
	
	
}
