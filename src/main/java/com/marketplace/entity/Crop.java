package com.marketplace.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crops")
public class Crop {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Column(nullable = false) private String name;
    @NotBlank @Column(nullable = false) private String category;
    @NotBlank @Column(nullable = false) private String farmerName;
    @NotBlank @Column(nullable = false) private String location;
    @Column(length = 1000) private String description;
    @NotNull @DecimalMin("0.01") @Column(nullable = false) private Double pricePerKg;
    @NotNull @DecimalMin("0.01") @Column(nullable = false) private Double availableKg;
    @Column(nullable = false) private LocalDateTime createdAt;

    public Crop() { }
    @PrePersist public void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(Double pricePerKg) { this.pricePerKg = pricePerKg; }
    public Double getAvailableKg() { return availableKg; }
    public void setAvailableKg(Double availableKg) { this.availableKg = availableKg; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
