package com.diviso.graeshoppe.service.dto;
import java.time.Instant;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Sale entity.
 */
public class SaleDTO implements Serializable {

    private Long id;

    private String saleUniqueId;

    private String idpCode;

    private String storeName;

    private Long customerId;

    private Instant date;

    private String paymentRef;

    private String paymentMode;

    private Double grandTotal;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSaleUniqueId() {
        return saleUniqueId;
    }

    public void setSaleUniqueId(String saleUniqueId) {
        this.saleUniqueId = saleUniqueId;
    }

    public String getIdpCode() {
        return idpCode;
    }

    public void setIdpCode(String idpCode) {
        this.idpCode = idpCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SaleDTO saleDTO = (SaleDTO) o;
        if (saleDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), saleDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SaleDTO{" +
            "id=" + getId() +
            ", saleUniqueId='" + getSaleUniqueId() + "'" +
            ", idpCode='" + getIdpCode() + "'" +
            ", storeName='" + getStoreName() + "'" +
            ", customerId=" + getCustomerId() +
            ", date='" + getDate() + "'" +
            ", paymentRef='" + getPaymentRef() + "'" +
            ", paymentMode='" + getPaymentMode() + "'" +
            ", grandTotal=" + getGrandTotal() +
            "}";
    }
}
