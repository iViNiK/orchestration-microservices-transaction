package it.vinicioflamini.omt.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import it.vinicioflamini.omt.common.entity.Item;

@Repository
public interface InventoryRepository extends JpaRepository<Item, Long> {

}
