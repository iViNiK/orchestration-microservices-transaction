package it.vinicioflamini.omt.order.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import it.vinicioflamini.omt.order.OrderServiceApplication;
import it.vinicioflamini.omt.order.controller.OrderController;

/*
 * This class does a simple sanity check by calling default HealthCheck endpoint 
 * (Spring Boot Actuator must be present) test that will fail if the application 
 * context cannot start. It also ensures that the context is creating controllers.
 * 
 * @author: Vinicio Flamini (io@vinicioflamini.it)
 * 
 * */
@SpringBootTest(classes = OrderServiceApplication.class)
@AutoConfigureMockMvc
public class TestingWebApplicationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrderController orderController;

	@Test
	public void contextLoads() throws Exception {
		assertThat(orderController).isNotNull();
	}

	@Test
	public void shouldReturnHealthCheckMessage() throws Exception {
		this.mockMvc.perform(get("/actuator/health")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString("{\"status\":\"UP\"}")));
	}

}
