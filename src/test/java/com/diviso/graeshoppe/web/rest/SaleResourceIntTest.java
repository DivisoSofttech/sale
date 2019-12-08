package com.diviso.graeshoppe.web.rest;

import com.diviso.graeshoppe.SaleApp;

import com.diviso.graeshoppe.domain.Sale;
import com.diviso.graeshoppe.repository.SaleRepository;
import com.diviso.graeshoppe.repository.search.SaleSearchRepository;
import com.diviso.graeshoppe.service.SaleService;
import com.diviso.graeshoppe.service.dto.SaleDTO;
import com.diviso.graeshoppe.service.mapper.SaleMapper;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Test class for the SaleResource REST controller.
 *
 * @see SaleResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SaleApp.class)
public class SaleResourceIntTest {

    private static final String DEFAULT_SALE_UNIQUE_ID = "AAAAAAAAAA";
    private static final String UPDATED_SALE_UNIQUE_ID = "BBBBBBBBBB";

    private static final String DEFAULT_IDP_CODE = "AAAAAAAAAA";
    private static final String UPDATED_IDP_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_STORE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_STORE_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final Long UPDATED_CUSTOMER_ID = 2L;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_PAYMENT_REF = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_REF = "BBBBBBBBBB";

    private static final String DEFAULT_PAYMENT_MODE = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_MODE = "BBBBBBBBBB";

    private static final Double DEFAULT_GRAND_TOTAL = 1D;
    private static final Double UPDATED_GRAND_TOTAL = 2D;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleMapper saleMapper;

    @Autowired
    private SaleService saleService;

    /**
     * This repository is mocked in the com.diviso.graeshoppe.repository.search test package.
     *
     * @see com.diviso.graeshoppe.repository.search.SaleSearchRepositoryMockConfiguration
     */
    @Autowired
    private SaleSearchRepository mockSaleSearchRepository;

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

    private MockMvc restSaleMockMvc;

    private Sale sale;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SaleResource saleResource = new SaleResource(saleService);
        this.restSaleMockMvc = MockMvcBuilders.standaloneSetup(saleResource)
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
    public static Sale createEntity(EntityManager em) {
        Sale sale = new Sale()
            .saleUniqueId(DEFAULT_SALE_UNIQUE_ID)
            .idpCode(DEFAULT_IDP_CODE)
            .storeName(DEFAULT_STORE_NAME)
            .customerId(DEFAULT_CUSTOMER_ID)
            .date(DEFAULT_DATE)
            .paymentRef(DEFAULT_PAYMENT_REF)
            .paymentMode(DEFAULT_PAYMENT_MODE)
            .grandTotal(DEFAULT_GRAND_TOTAL);
        return sale;
    }

    @Before
    public void initTest() {
        sale = createEntity(em);
    }

    @Test
    @Transactional
    public void createSale() throws Exception {
        int databaseSizeBeforeCreate = saleRepository.findAll().size();

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);
        restSaleMockMvc.perform(post("/api/sales")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(saleDTO)))
            .andExpect(status().isCreated());

        // Validate the Sale in the database
        List<Sale> saleList = saleRepository.findAll();
        assertThat(saleList).hasSize(databaseSizeBeforeCreate + 1);
        Sale testSale = saleList.get(saleList.size() - 1);
        assertThat(testSale.getSaleUniqueId()).isEqualTo(DEFAULT_SALE_UNIQUE_ID);
        assertThat(testSale.getIdpCode()).isEqualTo(DEFAULT_IDP_CODE);
        assertThat(testSale.getStoreName()).isEqualTo(DEFAULT_STORE_NAME);
        assertThat(testSale.getCustomerId()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testSale.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testSale.getPaymentRef()).isEqualTo(DEFAULT_PAYMENT_REF);
        assertThat(testSale.getPaymentMode()).isEqualTo(DEFAULT_PAYMENT_MODE);
        assertThat(testSale.getGrandTotal()).isEqualTo(DEFAULT_GRAND_TOTAL);

        // Validate the Sale in Elasticsearch
        verify(mockSaleSearchRepository, times(1)).save(testSale);
    }

    @Test
    @Transactional
    public void createSaleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = saleRepository.findAll().size();

        // Create the Sale with an existing ID
        sale.setId(1L);
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSaleMockMvc.perform(post("/api/sales")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        List<Sale> saleList = saleRepository.findAll();
        assertThat(saleList).hasSize(databaseSizeBeforeCreate);

        // Validate the Sale in Elasticsearch
        verify(mockSaleSearchRepository, times(0)).save(sale);
    }

    @Test
    @Transactional
    public void getAllSales() throws Exception {
        // Initialize the database
        saleRepository.saveAndFlush(sale);

        // Get all the saleList
        restSaleMockMvc.perform(get("/api/sales?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sale.getId().intValue())))
            .andExpect(jsonPath("$.[*].saleUniqueId").value(hasItem(DEFAULT_SALE_UNIQUE_ID.toString())))
            .andExpect(jsonPath("$.[*].idpCode").value(hasItem(DEFAULT_IDP_CODE.toString())))
            .andExpect(jsonPath("$.[*].storeName").value(hasItem(DEFAULT_STORE_NAME.toString())))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID.intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].paymentRef").value(hasItem(DEFAULT_PAYMENT_REF.toString())))
            .andExpect(jsonPath("$.[*].paymentMode").value(hasItem(DEFAULT_PAYMENT_MODE.toString())))
            .andExpect(jsonPath("$.[*].grandTotal").value(hasItem(DEFAULT_GRAND_TOTAL.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getSale() throws Exception {
        // Initialize the database
        saleRepository.saveAndFlush(sale);

        // Get the sale
        restSaleMockMvc.perform(get("/api/sales/{id}", sale.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(sale.getId().intValue()))
            .andExpect(jsonPath("$.saleUniqueId").value(DEFAULT_SALE_UNIQUE_ID.toString()))
            .andExpect(jsonPath("$.idpCode").value(DEFAULT_IDP_CODE.toString()))
            .andExpect(jsonPath("$.storeName").value(DEFAULT_STORE_NAME.toString()))
            .andExpect(jsonPath("$.customerId").value(DEFAULT_CUSTOMER_ID.intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.paymentRef").value(DEFAULT_PAYMENT_REF.toString()))
            .andExpect(jsonPath("$.paymentMode").value(DEFAULT_PAYMENT_MODE.toString()))
            .andExpect(jsonPath("$.grandTotal").value(DEFAULT_GRAND_TOTAL.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingSale() throws Exception {
        // Get the sale
        restSaleMockMvc.perform(get("/api/sales/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSale() throws Exception {
        // Initialize the database
        saleRepository.saveAndFlush(sale);

        int databaseSizeBeforeUpdate = saleRepository.findAll().size();

        // Update the sale
        Sale updatedSale = saleRepository.findById(sale.getId()).get();
        // Disconnect from session so that the updates on updatedSale are not directly saved in db
        em.detach(updatedSale);
        updatedSale
            .saleUniqueId(UPDATED_SALE_UNIQUE_ID)
            .idpCode(UPDATED_IDP_CODE)
            .storeName(UPDATED_STORE_NAME)
            .customerId(UPDATED_CUSTOMER_ID)
            .date(UPDATED_DATE)
            .paymentRef(UPDATED_PAYMENT_REF)
            .paymentMode(UPDATED_PAYMENT_MODE)
            .grandTotal(UPDATED_GRAND_TOTAL);
        SaleDTO saleDTO = saleMapper.toDto(updatedSale);

        restSaleMockMvc.perform(put("/api/sales")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(saleDTO)))
            .andExpect(status().isOk());

        // Validate the Sale in the database
        List<Sale> saleList = saleRepository.findAll();
        assertThat(saleList).hasSize(databaseSizeBeforeUpdate);
        Sale testSale = saleList.get(saleList.size() - 1);
        assertThat(testSale.getSaleUniqueId()).isEqualTo(UPDATED_SALE_UNIQUE_ID);
        assertThat(testSale.getIdpCode()).isEqualTo(UPDATED_IDP_CODE);
        assertThat(testSale.getStoreName()).isEqualTo(UPDATED_STORE_NAME);
        assertThat(testSale.getCustomerId()).isEqualTo(UPDATED_CUSTOMER_ID);
        assertThat(testSale.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testSale.getPaymentRef()).isEqualTo(UPDATED_PAYMENT_REF);
        assertThat(testSale.getPaymentMode()).isEqualTo(UPDATED_PAYMENT_MODE);
        assertThat(testSale.getGrandTotal()).isEqualTo(UPDATED_GRAND_TOTAL);

        // Validate the Sale in Elasticsearch
        verify(mockSaleSearchRepository, times(1)).save(testSale);
    }

    @Test
    @Transactional
    public void updateNonExistingSale() throws Exception {
        int databaseSizeBeforeUpdate = saleRepository.findAll().size();

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleMockMvc.perform(put("/api/sales")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        List<Sale> saleList = saleRepository.findAll();
        assertThat(saleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sale in Elasticsearch
        verify(mockSaleSearchRepository, times(0)).save(sale);
    }

    @Test
    @Transactional
    public void deleteSale() throws Exception {
        // Initialize the database
        saleRepository.saveAndFlush(sale);

        int databaseSizeBeforeDelete = saleRepository.findAll().size();

        // Delete the sale
        restSaleMockMvc.perform(delete("/api/sales/{id}", sale.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Sale> saleList = saleRepository.findAll();
        assertThat(saleList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Sale in Elasticsearch
        verify(mockSaleSearchRepository, times(1)).deleteById(sale.getId());
    }

    @Test
    @Transactional
    public void searchSale() throws Exception {
        // Initialize the database
        saleRepository.saveAndFlush(sale);
        when(mockSaleSearchRepository.search(queryStringQuery("id:" + sale.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(sale), PageRequest.of(0, 1), 1));
        // Search the sale
        restSaleMockMvc.perform(get("/api/_search/sales?query=id:" + sale.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sale.getId().intValue())))
            .andExpect(jsonPath("$.[*].saleUniqueId").value(hasItem(DEFAULT_SALE_UNIQUE_ID)))
            .andExpect(jsonPath("$.[*].idpCode").value(hasItem(DEFAULT_IDP_CODE)))
            .andExpect(jsonPath("$.[*].storeName").value(hasItem(DEFAULT_STORE_NAME)))
            .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID.intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].paymentRef").value(hasItem(DEFAULT_PAYMENT_REF)))
            .andExpect(jsonPath("$.[*].paymentMode").value(hasItem(DEFAULT_PAYMENT_MODE)))
            .andExpect(jsonPath("$.[*].grandTotal").value(hasItem(DEFAULT_GRAND_TOTAL.doubleValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sale.class);
        Sale sale1 = new Sale();
        sale1.setId(1L);
        Sale sale2 = new Sale();
        sale2.setId(sale1.getId());
        assertThat(sale1).isEqualTo(sale2);
        sale2.setId(2L);
        assertThat(sale1).isNotEqualTo(sale2);
        sale1.setId(null);
        assertThat(sale1).isNotEqualTo(sale2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaleDTO.class);
        SaleDTO saleDTO1 = new SaleDTO();
        saleDTO1.setId(1L);
        SaleDTO saleDTO2 = new SaleDTO();
        assertThat(saleDTO1).isNotEqualTo(saleDTO2);
        saleDTO2.setId(saleDTO1.getId());
        assertThat(saleDTO1).isEqualTo(saleDTO2);
        saleDTO2.setId(2L);
        assertThat(saleDTO1).isNotEqualTo(saleDTO2);
        saleDTO1.setId(null);
        assertThat(saleDTO1).isNotEqualTo(saleDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(saleMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(saleMapper.fromId(null)).isNull();
    }
}
