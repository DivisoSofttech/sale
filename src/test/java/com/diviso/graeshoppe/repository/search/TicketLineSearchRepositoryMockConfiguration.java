package com.diviso.graeshoppe.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of TicketLineSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class TicketLineSearchRepositoryMockConfiguration {

    @MockBean
    private TicketLineSearchRepository mockTicketLineSearchRepository;

}
