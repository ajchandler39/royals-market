package com.royalsmarket.repository;

import com.royalsmarket.entity.CatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {

    List<CatalogItem> findAllByOrderByCategoryAscNameAsc();

    boolean existsByNameIgnoreCase(String name);
}
