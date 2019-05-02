package com.diviso.graeshoppe.repository;

import com.diviso.graeshoppe.domain.TicketLine;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data  repository for the TicketLine entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketLineRepository extends JpaRepository<TicketLine, Long> {

	List<TicketLine> findBySaleId(@Param("saleId")Long saleId);
}
