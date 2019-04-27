package com.diviso.graeshoppe.repository;

import com.diviso.graeshoppe.domain.TicketLine;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the TicketLine entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketLineRepository extends JpaRepository<TicketLine, Long> {

}
