package com.diviso.graeshoppe.web.rest;
import com.diviso.graeshoppe.service.SaleService;
import com.diviso.graeshoppe.web.rest.errors.BadRequestAlertException;
import com.diviso.graeshoppe.web.rest.util.HeaderUtil;
import com.diviso.graeshoppe.web.rest.util.PaginationUtil;
import com.diviso.graeshoppe.service.dto.SaleDTO;
import io.github.jhipster.web.util.ResponseUtil;
import net.sf.jasperreports.engine.JRException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Sale.
 */
@RestController
@RequestMapping("/api")
public class SaleResource {

    private final Logger log = LoggerFactory.getLogger(SaleResource.class);

    private static final String ENTITY_NAME = "saleSale";

    private final SaleService saleService;

    public SaleResource(SaleService saleService) {
        this.saleService = saleService;
    }

    /**
     * POST  /sales : Create a new sale.
     *
     * @param saleDTO the saleDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new saleDTO, or with status 400 (Bad Request) if the sale has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/sales")
    public ResponseEntity<SaleDTO> createSale(@RequestBody SaleDTO saleDTO) throws URISyntaxException {
        log.debug("REST request to save Sale : {}", saleDTO);
        if (saleDTO.getId() != null) {
            throw new BadRequestAlertException("A new sale cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SaleDTO result = saleService.save(saleDTO);
        return ResponseEntity.created(new URI("/api/sales/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sales : Updates an existing sale.
     *
     * @param saleDTO the saleDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated saleDTO,
     * or with status 400 (Bad Request) if the saleDTO is not valid,
     * or with status 500 (Internal Server Error) if the saleDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/sales")
    public ResponseEntity<SaleDTO> updateSale(@RequestBody SaleDTO saleDTO) throws URISyntaxException {
        log.debug("REST request to update Sale : {}", saleDTO);
        if (saleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SaleDTO result = saleService.save(saleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, saleDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sales : get all the sales.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of sales in body
     */
    @GetMapping("/sales")
    public ResponseEntity<List<SaleDTO>> getAllSales(Pageable pageable) {
        log.debug("REST request to get a page of Sales");
        Page<SaleDTO> page = saleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sales");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /sales/:id : get the "id" sale.
     *
     * @param id the id of the saleDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the saleDTO, or with status 404 (Not Found)
     */
    @GetMapping("/sales/{id}")
    public ResponseEntity<SaleDTO> getSale(@PathVariable Long id) {
        log.debug("REST request to get Sale : {}", id);
        Optional<SaleDTO> saleDTO = saleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(saleDTO);
    }

    /**
     * DELETE  /sales/:id : delete the "id" sale.
     *
     * @param id the id of the saleDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/sales/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        log.debug("REST request to delete Sale : {}", id);
        saleService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/sales?query=:query : search for the sale corresponding
     * to the query.
     *
     * @param query the query of the sale search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/sales")
    public ResponseEntity<List<SaleDTO>> searchSales(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Sales for query {}", query);
        Page<SaleDTO> page = saleService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/sales");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
    
    
	 @GetMapping("/printSale/{saleId}/{idpCode}")
	 public ResponseEntity<byte[]> printSale(@PathVariable Long saleId,@PathVariable String idpCode) {
	     

			log.debug("REST request to get a pdf of sale");

			byte[] pdfContents = null;
			
			 try
		      {
		        pdfContents=saleService.getSaleReportAsPdf(saleId, idpCode);
		      }
		      catch (JRException e) {
		           e.printStackTrace();
		      }
		     

	
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType("application/pdf"));
			String fileName = "bill.pdf";
			headers.add("content-disposition", "attachment; filename=" + fileName);
			ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(pdfContents, headers, HttpStatus.OK);
			return response;
		}

}
