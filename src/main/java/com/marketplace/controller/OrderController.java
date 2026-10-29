package com.marketplace.controller;

import com.marketplace.entity.Crop;
import com.marketplace.entity.MarketplaceOrder;
import com.marketplace.entity.UserAccount;
import com.marketplace.repository.CropRepository;
import com.marketplace.repository.MarketplaceOrderRepository;
import com.marketplace.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final MarketplaceOrderRepository orders;
    private final CropRepository crops;
    private final UserAccountRepository users;

    public OrderController(MarketplaceOrderRepository orders, CropRepository crops, UserAccountRepository users) {
        this.orders = orders;
        this.crops = crops;
        this.users = users;
    }

    public static class CreateOrder {
        public Long cropId;
        public Double quantity;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody CreateOrder request, Authentication auth) {
        if (request == null || request.cropId == null || request.quantity == null ||
                !Double.isFinite(request.quantity) || request.quantity <= 0) {
            return ResponseEntity.badRequest().body(error("Crop and a positive quantity are required."));
        }
        Optional<Crop> found = crops.findByIdForUpdate(request.cropId);
        if (!found.isPresent()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Crop not found."));
        Crop crop = found.get();
        if (request.quantity > crop.getAvailableKg()) {
            return ResponseEntity.badRequest().body(error("Requested quantity exceeds available stock."));
        }

        // Reserve stock in the same transaction as order creation to prevent overselling.
        crop.setAvailableKg(crop.getAvailableKg() - request.quantity);
        crops.save(crop);

        MarketplaceOrder order = new MarketplaceOrder();
        order.setCropId(crop.getId());
        order.setCropName(crop.getName());
        order.setBuyerEmail(auth.getName());
        Optional<UserAccount> buyer = users.findByEmailIgnoreCase(auth.getName());
        order.setBuyerName(buyer.isPresent() ? buyer.get().getName() : auth.getName());
        order.setQuantity(request.quantity);
        order.setPricePerKg(crop.getPricePerKg());
        order.setTotalAmount(request.quantity * crop.getPricePerKg());
        order.setStatus("Pending");
        return ResponseEntity.status(HttpStatus.CREATED).body(orders.save(order));
    }

    @GetMapping
    public ResponseEntity<?> list(Authentication auth) {
        return ResponseEntity.ok(orders.findByBuyerEmailIgnoreCaseOrderByCreatedAtDesc(auth.getName()));
    }

    @PutMapping("/{id}/status")
    @Transactional
    public ResponseEntity<?> status(@PathVariable Long id, @RequestParam String value, Authentication auth) {
        if (!auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error("Only an admin can update order status."));
        }
        Optional<MarketplaceOrder> found = orders.findById(id);
        if (!found.isPresent()) return ResponseEntity.notFound().build();
        MarketplaceOrder order = found.get();
        String oldStatus = order.getStatus();
        String newStatus = value == null ? "" : value.trim();
        if (!isAllowedTransition(oldStatus, newStatus)) {
            return ResponseEntity.badRequest().body(error("Invalid order status transition from " + oldStatus + " to " + newStatus + "."));
        }

        // Restore reserved stock exactly once when an order is rejected or cancelled before shipping.
        if (("Pending".equals(oldStatus) || "Accepted".equals(oldStatus)) &&
                ("Rejected".equals(newStatus) || "Cancelled".equals(newStatus))) {
            Optional<Crop> cropFound = crops.findByIdForUpdate(order.getCropId());
            if (cropFound.isPresent()) {
                Crop crop = cropFound.get();
                crop.setAvailableKg(crop.getAvailableKg() + order.getQuantity());
                crops.save(crop);
            }
        }
        order.setStatus(newStatus);
        return ResponseEntity.ok(orders.save(order));
    }

    private boolean isAllowedTransition(String oldStatus, String next) {
        if ("Pending".equals(oldStatus)) return Arrays.asList("Accepted", "Rejected", "Cancelled").contains(next);
        if ("Accepted".equals(oldStatus)) return Arrays.asList("Shipped", "Cancelled").contains(next);
        if ("Shipped".equals(oldStatus)) return "Delivered".equals(next);
        return false;
    }

    private Map<String, String> error(String message) {
        Map<String, String> result = new HashMap<String, String>();
        result.put("message", message);
        return result;
    }
}
