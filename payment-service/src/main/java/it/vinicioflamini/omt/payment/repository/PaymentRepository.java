package it.vinicioflamini.omt.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.vinicioflamini.omt.common.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
