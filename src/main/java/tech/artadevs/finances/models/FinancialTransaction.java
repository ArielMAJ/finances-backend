package tech.artadevs.finances.models;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import tech.artadevs.finances.dtos.FinancialTransactionResponseDto;

@Entity
@Table(name = "financial_transactions")
public class FinancialTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private String description;

    @Column(name = "deleted_at", nullable = true)
    private Date deletedAt = null;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at", nullable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public FinancialTransaction() {
    }

    public FinancialTransaction(Double value, String description, User user) {
        this.value = value;
        this.description = description;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public FinancialTransaction setId(Long id) {
        this.id = id;
        return this;
    }

    public Double getValue() {
        return value;
    }

    public FinancialTransaction setValue(Double value) {
        this.value = value;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public FinancialTransaction setDescription(String description) {
        this.description = description;
        return this;
    }

    public User getUser() {
        return user;
    }

    public FinancialTransaction setUser(User user) {
        this.user = user;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public FinancialTransaction setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public FinancialTransaction setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public FinancialTransaction setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public FinancialTransactionResponseDto toFinancialTransactionResponseDto() {
        return new FinancialTransactionResponseDto()
                .setId(id)
                .setDescription(description)
                .setValue(value)
                .setCreatedAt(createdAt)
                .setUpdatedAt(updatedAt);

    }
}
