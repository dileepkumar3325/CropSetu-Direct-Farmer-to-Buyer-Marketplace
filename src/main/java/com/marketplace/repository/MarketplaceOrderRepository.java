package com.marketplace.repository;

import com.marketplace.entity.MarketplaceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MarketplaceOrderRepository extends JpaRepository<MarketplaceOrder, Long> {
	List<MarketplaceOrder> findByBuyerEmailIgnoreCaseOrderByCreatedAtDesc(String email);
}
