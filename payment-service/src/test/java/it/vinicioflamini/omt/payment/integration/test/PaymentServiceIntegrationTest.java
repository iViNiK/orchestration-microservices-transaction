package it.vinicioflamini.omt.payment.integration.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.domain.DomainObjects;
import it.vinicioflamini.omt.common.domain.OutboxProxy;
import it.vinicioflamini.omt.common.entity.Payment;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.payment.domain.PaymentFacade;
import it.vinicioflamini.omt.payment.repository.PaymentRepository;
import it.vinicioflamini.omt.payment.service.PaymentService;

@RunWith(SpringRunner.class)
@DirtiesContext
public class PaymentServiceIntegrationTest {
	@TestConfiguration
	static class InventoryServiceIntegrationTestContextConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public PaymentService paymentService() {
			return new PaymentService();
		}

		@Bean
		@ConditionalOnMissingBean
		public PaymentFacade paymentFacade() {
			return new PaymentFacade();
		}

	}

	@Autowired
	private PaymentService paymentService;

	@MockBean
	private OutboxProxy outboxProxy;

	@MockBean
	private PaymentRepository paymentRepository;

	@Test
	public void testCreatePaymentException() throws JsonProcessingException {
		Payment payment = new Payment(10L, 10L, 10L, 10L);
		
		when(paymentRepository.save(payment)).thenThrow(new EntityNotFoundException());
		
		Long paymentId = paymentService.makePayment(10L, 10L, 10L);
		
		if (paymentId != null) {
			verify(outboxProxy, times(0)).requestMessage(null, DomainObjects.PAYMENT, new OrderEvent());
		} else {
			verify(outboxProxy, times(0)).requestMessage(null, DomainObjects.PAYMENT, new OrderEvent());
		}
	}

	@Test
	public void testCreatePaymentOkThenPublishPaymentReceivedEventElsePublishPaymentFailedEvent() throws JsonProcessingException {
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setOrderId(10L);
		
		Payment payment = new Payment(10L, 10L, 10L, 10L);
		
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(payment.getOrderId());
		orderEvent.setItemId(payment.getItemId());
		orderEvent.setCustomerId(payment.getCustomerId());
		
		Long paymentId = paymentService.makePayment(10L, 10L, 10L);
		
		if (paymentId != null) {
			payment.setPaymentId(paymentId);
			payment.setApproved(Boolean.TRUE);
			orderEvent.setAction(Action.PAYMENTRECEIVED);
		} else {
			payment.setPaymentId(null);
			payment.setApproved(Boolean.FALSE);
			orderEvent.setAction(Action.PAYMENTFAILED);
		}

		when(paymentRepository.save(payment)).thenReturn(payment);
		orderEvent.setPaymentId(payment.getId());
		verify(outboxProxy, times(1)).requestMessage(null, DomainObjects.PAYMENT, orderEvent);	

	}

}
