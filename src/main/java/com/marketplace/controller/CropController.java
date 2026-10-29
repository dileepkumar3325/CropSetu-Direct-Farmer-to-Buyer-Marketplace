package com.marketplace.controller;

import com.marketplace.entity.Crop;
import com.marketplace.service.CropService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/crops")
public class CropController {
	private final CropService service;

	public CropController(CropService service) {
		this.service = service;
	}

	@GetMapping
	public List<Crop> getCrops(@RequestParam(required = false) String q,
			@RequestParam(required = false) String location) {
		return service.findAll(q, location);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Crop> getCrop(@PathVariable Long id) {
		Optional<Crop> crop = service.findById(id);
		return crop.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Crop> addCrop(@Valid @RequestBody Crop crop) {
		crop.setId(null);
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(crop));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Crop> updateCrop(@PathVariable Long id, @Valid @RequestBody Crop input) {
		Optional<Crop> existing = service.findById(id);
		if (!existing.isPresent())
			return ResponseEntity.notFound().build();
		input.setId(id);
		return ResponseEntity.ok(service.save(input));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCrop(@PathVariable Long id) {
		if (!service.findById(id).isPresent())
			return ResponseEntity.notFound().build();
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
