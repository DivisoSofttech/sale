package com.diviso.graeshoppe.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Sale.
 */
@Entity
@Table(name = "sale")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "sale")
public class Sale implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sale_unique_id")
    private String saleUniqueId;

    @Column(name = "idp_code")
    private String idpCode;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "jhi_date")
    private Instant date;

    @Column(name = "payment_ref")
    private String paymentRef;

    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name = "grand_total")
    private Double grandTotal;

    @OneToMany(mappedBy = "sale")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<TicketLine> ticketLines = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSaleUniqueId() {
        return saleUniqueId;
    }

    public Sale saleUniqueId(String saleUniqueId) {
        this.saleUniqueId = saleUniqueId;
        return this;
    }

    public void setSaleUniqueId(String saleUniqueId) {
        this.saleUniqueId = saleUniqueId;
    }

    public String getIdpCode() {
        return idpCode;
    }

    public Sale idpCode(String idpCode) {
        this.idpCode = idpCode;
        return this;
    }

    public void setIdpCode(String idpCode) {
        this.idpCode = idpCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public Sale storeName(String storeName) {
        this.storeName = storeName;
        return this;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Sale customerId(Long customerId) {
        this.customerId = customerId;
        return this;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Instant getDate() {
        return date;
    }

    public Sale date(Instant date) {
        this.date = date;
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

    public Sale paymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
        return this;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public Sale paymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
        return this;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Double getGrandTotal() {
        return grandTotal;
    }

    public Sale grandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
        return this;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public Set<TicketLine> getTicketLines() {
        return ticketLines;
    }

    public Sale ticketLines(Set<TicketLine> ticketLines) {
        this.ticketLines = ticketLines;
        return this;
    }

    public Sale addTicketLine(TicketLine ticketLine) {
        this.ticketLines.add(ticketLine);
        ticketLine.setSale(this);
        return this;
    }

    public Sale removeTicketLine(TicketLine ticketLine) {
        this.ticketLines.remove(ticketLine);
        ticketLine.setSale(null);
        return this;
    }

    public void setTicketLines(Set<TicketLine> ticketLines) {
        this.ticketLines = ticketLines;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sale sale = (Sale) o;
        if (sale.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), sale.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Sale{" +
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
