package com.marketplace.repository;

import com.marketplace.entity.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CropRepository extends JpaRepository<Crop, Long> {
	@Query("SELECT c FROM Crop c WHERE (:q IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(c.category) LIKE LOWER(CONCAT('%', :q, '%'))) AND (:location IS NULL OR LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%'))) ORDER BY c.createdAt DESC")
	List<Crop> search(@Param("q") String q, @Param("location") String location);
}
