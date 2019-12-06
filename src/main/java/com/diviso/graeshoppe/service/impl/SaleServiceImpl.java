package com.diviso.graeshoppe.service.impl;

import com.diviso.graeshoppe.service.SaleService;
import com.diviso.graeshoppe.domain.Sale;
import com.diviso.graeshoppe.domain.TicketLine;

import com.diviso.graeshoppe.repository.SaleRepository;
import com.diviso.graeshoppe.repository.TicketLineRepository;
import com.diviso.graeshoppe.repository.search.SaleSearchRepository;
import com.diviso.graeshoppe.service.dto.SaleDTO;
import com.diviso.graeshoppe.service.mapper.SaleMapper;
import com.diviso.graeshoppe.avro.Sale.Builder;
import com.diviso.graeshoppe.config.MessageBinderConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Sale.
 */
@Service
@Transactional
public class SaleServiceImpl implements SaleService {

    private final Logger log = LoggerFactory.getLogger(SaleServiceImpl.class);

    private final SaleRepository saleRepository;
    
	private final TicketLineRepository ticketLineRepository;
	
	@Autowired
	private MessageBinderConfiguration messageChannel;

    private final SaleMapper saleMapper;

    private final SaleSearchRepository saleSearchRepository;

    public SaleServiceImpl(SaleRepository saleRepository, SaleMapper saleMapper, 
    		SaleSearchRepository saleSearchRepository, TicketLineRepository ticketLineRepository) {
        this.saleRepository = saleRepository;
        this.saleMapper = saleMapper;
        this.saleSearchRepository = saleSearchRepository;
        this.ticketLineRepository = ticketLineRepository;
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
        publishMesssage(sale.getId());
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
    
	public com.diviso.graeshoppe.avro.TicketLine toAvroTicketLine(TicketLine ticketline) {
		return com.diviso.graeshoppe.avro.TicketLine.newBuilder()
				.setPrice(ticketline.getPrice())
				.setProductId(ticketline.getProductId())
				.setTotal(ticketline.getTotal()).build();
	}
    
	@Override
	public boolean publishMesssage(Long saleId) {
		Sale sale = saleRepository.findById(saleId).get();
		sale.setTicketLines(new HashSet<TicketLine>(ticketLineRepository.findBySaleId(saleId)));
		Builder saleAvro = com.diviso.graeshoppe.avro.Sale.newBuilder()
				.setCustomerId(sale.getCustomerId())
				.setDate(sale.getDate().toEpochMilli())
				.setGrandTotal(sale.getGrandTotal())
				.setUserId(sale.getUserId())
		
				.setTicketLines(sale.getTicketLines().stream()
		  .map(this::toAvroTicketLine).collect(Collectors.toList()));
		 
		com.diviso.graeshoppe.avro.Sale message =saleAvro.build();
		
		log.info("+++++++++++++++++++++++++++++++ completed publish");
		return messageChannel.saleOut().send(MessageBuilder.withPayload(message).build());

	}
	
}
