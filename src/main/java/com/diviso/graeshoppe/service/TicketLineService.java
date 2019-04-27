package com.diviso.graeshoppe.service;

import com.diviso.graeshoppe.service.dto.TicketLineDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing TicketLine.
 */
public interface TicketLineService {

    /**
     * Save a ticketLine.
     *
     * @param ticketLineDTO the entity to save
     * @return the persisted entity
     */
    TicketLineDTO save(TicketLineDTO ticketLineDTO);

    /**
     * Get all the ticketLines.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<TicketLineDTO> findAll(Pageable pageable);


    /**
     * Get the "id" ticketLine.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<TicketLineDTO> findOne(Long id);

    /**
     * Delete the "id" ticketLine.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the ticketLine corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<TicketLineDTO> search(String query, Pageable pageable);
}
