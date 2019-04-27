package com.diviso.graeshoppe.web.rest;

import com.diviso.graeshoppe.SalemicroserviceApp;

import com.diviso.graeshoppe.domain.TicketLine;
import com.diviso.graeshoppe.repository.TicketLineRepository;
import com.diviso.graeshoppe.repository.search.TicketLineSearchRepository;
import com.diviso.graeshoppe.service.SaleService;
import com.diviso.graeshoppe.service.TicketLineService;
import com.diviso.graeshoppe.service.dto.TicketLineDTO;
import com.diviso.graeshoppe.service.mapper.SaleMapper;
import com.diviso.graeshoppe.service.mapper.TicketLineMapper;
import com.diviso.graeshoppe.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static com.diviso.graeshoppe.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TicketLineResource REST controller.
 *
 * @see TicketLineResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SalemicroserviceApp.class)
public class TicketLineResourceIntTest {

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final Double DEFAULT_PRICE = 1D;
    private static final Double UPDATED_PRICE = 2D;

    private static final Double DEFAULT_TOTAL = 1D;
    private static final Double UPDATED_TOTAL = 2D;

    @Autowired
    private TicketLineRepository ticketLineRepository;

    @Autowired
    private TicketLineMapper ticketLineMapper;

    @Autowired
    private TicketLineService ticketLineService;
    @Autowired
    private  SaleMapper saleMapper;
    @Autowired
	private  SaleService saleService;

    /**
     * This repository is mocked in the com.diviso.graeshoppe.repository.search test package.
     *
     * @see com.diviso.graeshoppe.repository.search.TicketLineSearchRepositoryMockConfiguration
     */
    @Autowired
    private TicketLineSearchRepository mockTicketLineSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restTicketLineMockMvc;

    private TicketLine ticketLine;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TicketLineResource ticketLineResource = new TicketLineResource(ticketLineService,saleMapper,saleService);
        this.restTicketLineMockMvc = MockMvcBuilders.standaloneSetup(ticketLineResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketLine createEntity(EntityManager em) {
        TicketLine ticketLine = new TicketLine()
            .productId(DEFAULT_PRODUCT_ID)
            .quantity(DEFAULT_QUANTITY)
            .price(DEFAULT_PRICE)
            .total(DEFAULT_TOTAL);
        return ticketLine;
    }

    @Before
    public void initTest() {
        ticketLine = createEntity(em);
    }

    @Test
    @Transactional
    public void createTicketLine() throws Exception {
        int databaseSizeBeforeCreate = ticketLineRepository.findAll().size();

        // Create the TicketLine
        TicketLineDTO ticketLineDTO = ticketLineMapper.toDto(ticketLine);
        restTicketLineMockMvc.perform(post("/api/ticket-lines")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ticketLineDTO)))
            .andExpect(status().isCreated());

        // Validate the TicketLine in the database
        List<TicketLine> ticketLineList = ticketLineRepository.findAll();
        assertThat(ticketLineList).hasSize(databaseSizeBeforeCreate + 1);
        TicketLine testTicketLine = ticketLineList.get(ticketLineList.size() - 1);
        assertThat(testTicketLine.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testTicketLine.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testTicketLine.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testTicketLine.getTotal()).isEqualTo(DEFAULT_TOTAL);

        // Validate the TicketLine in Elasticsearch
        verify(mockTicketLineSearchRepository, times(1)).save(testTicketLine);
    }

    @Test
    @Transactional
    public void createTicketLineWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ticketLineRepository.findAll().size();

        // Create the TicketLine with an existing ID
        ticketLine.setId(1L);
        TicketLineDTO ticketLineDTO = ticketLineMapper.toDto(ticketLine);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketLineMockMvc.perform(post("/api/ticket-lines")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ticketLineDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TicketLine in the database
        List<TicketLine> ticketLineList = ticketLineRepository.findAll();
        assertThat(ticketLineList).hasSize(databaseSizeBeforeCreate);

        // Validate the TicketLine in Elasticsearch
        verify(mockTicketLineSearchRepository, times(0)).save(ticketLine);
    }

    @Test
    @Transactional
    public void getAllTicketLines() throws Exception {
        // Initialize the database
        ticketLineRepository.saveAndFlush(ticketLine);

        // Get all the ticketLineList
        restTicketLineMockMvc.perform(get("/api/ticket-lines?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getTicketLine() throws Exception {
        // Initialize the database
        ticketLineRepository.saveAndFlush(ticketLine);

        // Get the ticketLine
        restTicketLineMockMvc.perform(get("/api/ticket-lines/{id}", ticketLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(ticketLine.getId().intValue()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTicketLine() throws Exception {
        // Get the ticketLine
        restTicketLineMockMvc.perform(get("/api/ticket-lines/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTicketLine() throws Exception {
        // Initialize the database
        ticketLineRepository.saveAndFlush(ticketLine);

        int databaseSizeBeforeUpdate = ticketLineRepository.findAll().size();

        // Update the ticketLine
        TicketLine updatedTicketLine = ticketLineRepository.findById(ticketLine.getId()).get();
        // Disconnect from session so that the updates on updatedTicketLine are not directly saved in db
        em.detach(updatedTicketLine);
        updatedTicketLine
            .productId(UPDATED_PRODUCT_ID)
            .quantity(UPDATED_QUANTITY)
            .price(UPDATED_PRICE)
            .total(UPDATED_TOTAL);
        TicketLineDTO ticketLineDTO = ticketLineMapper.toDto(updatedTicketLine);

        restTicketLineMockMvc.perform(put("/api/ticket-lines")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ticketLineDTO)))
            .andExpect(status().isOk());

        // Validate the TicketLine in the database
        List<TicketLine> ticketLineList = ticketLineRepository.findAll();
        assertThat(ticketLineList).hasSize(databaseSizeBeforeUpdate);
        TicketLine testTicketLine = ticketLineList.get(ticketLineList.size() - 1);
        assertThat(testTicketLine.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testTicketLine.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testTicketLine.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testTicketLine.getTotal()).isEqualTo(UPDATED_TOTAL);

        // Validate the TicketLine in Elasticsearch
        verify(mockTicketLineSearchRepository, times(1)).save(testTicketLine);
    }

    @Test
    @Transactional
    public void updateNonExistingTicketLine() throws Exception {
        int databaseSizeBeforeUpdate = ticketLineRepository.findAll().size();

        // Create the TicketLine
        TicketLineDTO ticketLineDTO = ticketLineMapper.toDto(ticketLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketLineMockMvc.perform(put("/api/ticket-lines")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ticketLineDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TicketLine in the database
        List<TicketLine> ticketLineList = ticketLineRepository.findAll();
        assertThat(ticketLineList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TicketLine in Elasticsearch
        verify(mockTicketLineSearchRepository, times(0)).save(ticketLine);
    }

    @Test
    @Transactional
    public void deleteTicketLine() throws Exception {
        // Initialize the database
        ticketLineRepository.saveAndFlush(ticketLine);

        int databaseSizeBeforeDelete = ticketLineRepository.findAll().size();

        // Delete the ticketLine
        restTicketLineMockMvc.perform(delete("/api/ticket-lines/{id}", ticketLine.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<TicketLine> ticketLineList = ticketLineRepository.findAll();
        assertThat(ticketLineList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the TicketLine in Elasticsearch
        verify(mockTicketLineSearchRepository, times(1)).deleteById(ticketLine.getId());
    }

    @Test
    @Transactional
    public void searchTicketLine() throws Exception {
        // Initialize the database
        ticketLineRepository.saveAndFlush(ticketLine);
        when(mockTicketLineSearchRepository.search(queryStringQuery("id:" + ticketLine.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(ticketLine), PageRequest.of(0, 1), 1));
        // Search the ticketLine
        restTicketLineMockMvc.perform(get("/api/_search/ticket-lines?query=id:" + ticketLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketLine.class);
        TicketLine ticketLine1 = new TicketLine();
        ticketLine1.setId(1L);
        TicketLine ticketLine2 = new TicketLine();
        ticketLine2.setId(ticketLine1.getId());
        assertThat(ticketLine1).isEqualTo(ticketLine2);
        ticketLine2.setId(2L);
        assertThat(ticketLine1).isNotEqualTo(ticketLine2);
        ticketLine1.setId(null);
        assertThat(ticketLine1).isNotEqualTo(ticketLine2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketLineDTO.class);
        TicketLineDTO ticketLineDTO1 = new TicketLineDTO();
        ticketLineDTO1.setId(1L);
        TicketLineDTO ticketLineDTO2 = new TicketLineDTO();
        assertThat(ticketLineDTO1).isNotEqualTo(ticketLineDTO2);
        ticketLineDTO2.setId(ticketLineDTO1.getId());
        assertThat(ticketLineDTO1).isEqualTo(ticketLineDTO2);
        ticketLineDTO2.setId(2L);
        assertThat(ticketLineDTO1).isNotEqualTo(ticketLineDTO2);
        ticketLineDTO1.setId(null);
        assertThat(ticketLineDTO1).isNotEqualTo(ticketLineDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(ticketLineMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(ticketLineMapper.fromId(null)).isNull();
    }
}
