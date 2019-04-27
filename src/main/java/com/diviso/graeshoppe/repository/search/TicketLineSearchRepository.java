package com.diviso.graeshoppe.repository.search;

import com.diviso.graeshoppe.domain.TicketLine;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the TicketLine entity.
 */
public interface TicketLineSearchRepository extends ElasticsearchRepository<TicketLine, Long> {
}
