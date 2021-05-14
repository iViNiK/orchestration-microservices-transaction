package it.vinicioflamini.omt.order.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.vinicioflamini.omt.common.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}


