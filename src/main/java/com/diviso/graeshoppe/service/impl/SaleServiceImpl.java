package com.diviso.graeshoppe.service.impl;

import com.diviso.graeshoppe.service.SaleService;
import com.diviso.graeshoppe.domain.Sale;
import com.diviso.graeshoppe.repository.SaleRepository;
import com.diviso.graeshoppe.repository.search.SaleSearchRepository;
import com.diviso.graeshoppe.service.dto.SaleDTO;
import com.diviso.graeshoppe.service.mapper.SaleMapper;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Sale.
 */
@Service
@Transactional
public class SaleServiceImpl implements SaleService {
	
	@Autowired
    DataSource dataSource;
    private final Logger log = LoggerFactory.getLogger(SaleServiceImpl.class);

    private final SaleRepository saleRepository;

    private final SaleMapper saleMapper;

    private final SaleSearchRepository saleSearchRepository;

    public SaleServiceImpl(SaleRepository saleRepository, SaleMapper saleMapper, SaleSearchRepository saleSearchRepository) {
        this.saleRepository = saleRepository;
        this.saleMapper = saleMapper;
        this.saleSearchRepository = saleSearchRepository;
    }

    /**
     * Save a sale.
     *
     * @param saleDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public SaleDTO save(SaleDTO saleDTO) {
        log.debug("Request to save Sale : {}", saleDTO);
        Sale sale = saleMapper.toEntity(saleDTO);
        sale = saleRepository.save(sale);
        SaleDTO result = saleMapper.toDto(sale);
        saleSearchRepository.save(sale);
        return result;
    }

    /**
     * Get all the sales.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SaleDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Sales");
        return saleRepository.findAll(pageable)
            .map(saleMapper::toDto);
    }


    /**
     * Get one sale by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<SaleDTO> findOne(Long id) {
        log.debug("Request to get Sale : {}", id);
        return saleRepository.findById(id)
            .map(saleMapper::toDto);
    }

    /**
     * Delete the sale by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Sale : {}", id);        saleRepository.deleteById(id);
        saleSearchRepository.deleteById(id);
    }

    /**
     * Search for the sale corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SaleDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Sales for query {}", query);
        return saleSearchRepository.search(queryStringQuery(query), pageable)
            .map(saleMapper::toDto);
    }

	

	@Override
	public byte[] getSaleReportAsPdf(Long saleId, String idpCode) throws JRException {
		JasperReport jr = JasperCompileManager.compileReport("src/main/resources/report/sale.jrxml");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idp_code", idpCode);
		parameters.put("id",saleId);
				Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		JasperPrint jp = JasperFillManager.fillReport(jr, parameters, conn);
		return JasperExportManager.exportReportToPdf(jp);
	}
	}
