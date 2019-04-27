package com.diviso.graeshoppe.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the TicketLine entity.
 */
public class TicketLineDTO implements Serializable {

    private Long id;

    private Long productId;

    private Integer quantity;

    private Double price;

    private Double total;


    private Long saleId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = getPrice()*getQuantity();
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TicketLineDTO ticketLineDTO = (TicketLineDTO) o;
        if (ticketLineDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), ticketLineDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TicketLineDTO{" +
            "id=" + getId() +
            ", productId=" + getProductId() +
            ", quantity=" + getQuantity() +
            ", price=" + getPrice() +
            ", total=" + getTotal() +
            ", sale=" + getSaleId() +
            "}";
    }
}
