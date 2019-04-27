package com.diviso.graeshoppe.service.mapper;

import com.diviso.graeshoppe.domain.*;
import com.diviso.graeshoppe.service.dto.TicketLineDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity TicketLine and its DTO TicketLineDTO.
 */
@Mapper(componentModel = "spring", uses = {SaleMapper.class})
public interface TicketLineMapper extends EntityMapper<TicketLineDTO, TicketLine> {

    @Mapping(source = "sale.id", target = "saleId")
    TicketLineDTO toDto(TicketLine ticketLine);

    @Mapping(source = "saleId", target = "sale")
    TicketLine toEntity(TicketLineDTO ticketLineDTO);

    default TicketLine fromId(Long id) {
        if (id == null) {
            return null;
        }
        TicketLine ticketLine = new TicketLine();
        ticketLine.setId(id);
        return ticketLine;
    }
}
