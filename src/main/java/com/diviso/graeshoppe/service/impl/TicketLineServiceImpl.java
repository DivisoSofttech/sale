package com.diviso.graeshoppe.service.impl;

import com.diviso.graeshoppe.service.TicketLineService;
import com.diviso.graeshoppe.domain.TicketLine;
import com.diviso.graeshoppe.repository.TicketLineRepository;
import com.diviso.graeshoppe.repository.search.TicketLineSearchRepository;
import com.diviso.graeshoppe.service.dto.TicketLineDTO;
import com.diviso.graeshoppe.service.mapper.TicketLineMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing TicketLine.
 */
@Service
@Transactional
public class TicketLineServiceImpl implements TicketLineService {

    private final Logger log = LoggerFactory.getLogger(TicketLineServiceImpl.class);

    private final TicketLineRepository ticketLineRepository;

    private final TicketLineMapper ticketLineMapper;

    private final TicketLineSearchRepository ticketLineSearchRepository;

    public TicketLineServiceImpl(TicketLineRepository ticketLineRepository, TicketLineMapper ticketLineMapper, TicketLineSearchRepository ticketLineSearchRepository) {
        this.ticketLineRepository = ticketLineRepository;
        this.ticketLineMapper = ticketLineMapper;
        this.ticketLineSearchRepository = ticketLineSearchRepository;
    }

    /**
     * Save a ticketLine.
     *
     * @param ticketLineDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public TicketLineDTO save(TicketLineDTO ticketLineDTO) {
        log.debug("Request to save TicketLine : {}", ticketLineDTO);
        TicketLine ticketLine = ticketLineMapper.toEntity(ticketLineDTO);
        ticketLine = ticketLineRepository.save(ticketLine);
        TicketLineDTO result = ticketLineMapper.toDto(ticketLine);
        ticketLineSearchRepository.save(ticketLine);
        return result;
    }

    /**
     * Get all the ticketLines.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TicketLineDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TicketLines");
        return ticketLineRepository.findAll(pageable)
            .map(ticketLineMapper::toDto);
    }


    /**
     * Get one ticketLine by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TicketLineDTO> findOne(Long id) {
        log.debug("Request to get TicketLine : {}", id);
        return ticketLineRepository.findById(id)
            .map(ticketLineMapper::toDto);
    }

    /**
     * Delete the ticketLine by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete TicketLine : {}", id);        ticketLineRepository.deleteById(id);
        ticketLineSearchRepository.deleteById(id);
    }

    /**
     * Search for the ticketLine corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TicketLineDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TicketLines for query {}", query);
        return ticketLineSearchRepository.search(queryStringQuery(query), pageable)
            .map(ticketLineMapper::toDto);
    }
    
	/* (non-Javadoc)
	 * @see com.diviso.graeshoppe.service.TicketLineService#findBySaleId(java.lang.Long)
	 */
	@Override
	public List<TicketLineDTO> findBySaleId(Long saleId) {
		return ticketLineMapper.toDto(ticketLineRepository.findBySaleId(saleId));
	}
}
