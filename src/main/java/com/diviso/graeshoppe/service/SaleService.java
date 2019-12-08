package com.diviso.graeshoppe.service;

import com.diviso.graeshoppe.service.dto.SaleDTO;

import net.sf.jasperreports.engine.JRException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Sale.
 */
public interface SaleService {

    /**
     * Save a sale.
     *
     * @param saleDTO the entity to save
     * @return the persisted entity
     */
    SaleDTO save(SaleDTO saleDTO);

    /**
     * Get all the sales.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<SaleDTO> findAll(Pageable pageable);


    /**
     * Get the "id" sale.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<SaleDTO> findOne(Long id);

    /**
     * Delete the "id" sale.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the sale corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<SaleDTO> search(String query, Pageable pageable);

    
    /**
    * Get the sale report as pdf
    *
    * @param idpCode the idp code of the entity
    * @param saleId the id of the entity
    * @return the byte[]
    */
	

	byte[] getSaleReportAsPdf(Long saleId, String idpCode)throws JRException;
}
