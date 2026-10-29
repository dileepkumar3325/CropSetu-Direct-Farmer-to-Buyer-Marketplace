package com.marketplace.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "marketplace_orders")
public class MarketplaceOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private Long cropId;
	@Column(nullable = false)
	private String cropName;
	@Column(nullable = false)
	private String buyerEmail;
	@Column(nullable = false)
	private String buyerName;
	@Column(nullable = false)
	private Double quantity;
	@Column(nullable = false)
	private Double pricePerKg;
	@Column(nullable = false)
	private Double totalAmount;
	@Column(nullable = false)
	private String status;
	@Column(nullable = false)
	private LocalDateTime createdAt;

	public MarketplaceOrder() {
	}

	@PrePersist
	public void onCreate() {
		if (createdAt == null)
			createdAt = LocalDateTime.now();
		if (status == null)
			status = "Pending";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long v) {
		id = v;
	}

	public Long getCropId() {
		return cropId;
	}

	public void setCropId(Long v) {
		cropId = v;
	}

	public String getCropName() {
		return cropName;
	}

	public void setCropName(String v) {
		cropName = v;
	}

	public String getBuyerEmail() {
		return buyerEmail;
	}

	public void setBuyerEmail(String v) {
		buyerEmail = v;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String v) {
		buyerName = v;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double v) {
		quantity = v;
	}

	public Double getPricePerKg() {
		return pricePerKg;
	}

	public void setPricePerKg(Double v) {
		pricePerKg = v;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double v) {
		totalAmount = v;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String v) {
		status = v;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
