package com.marketplace.service;

import com.marketplace.entity.Crop;
import com.marketplace.repository.CropRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CropService {
	private final CropRepository repository;

	public CropService(CropRepository repository) {
		this.repository = repository;
	}

	public List<Crop> findAll(String q, String location) {
		return repository.search(clean(q), clean(location));
	}

	public Optional<Crop> findById(Long id) {
		return repository.findById(id);
	}

	public Crop save(Crop crop) {
		return repository.save(crop);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	private String clean(String value) {
		return value == null || value.trim().isEmpty() ? null : value.trim();
	}
}
