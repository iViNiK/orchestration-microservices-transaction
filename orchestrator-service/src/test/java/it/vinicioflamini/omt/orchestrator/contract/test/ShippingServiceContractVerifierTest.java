package it.vinicioflamini.omt.orchestrator.contract.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import it.vinicioflamini.omt.common.rest.payload.OrderRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext
@AutoConfigureStubRunner(
		  stubsMode = StubRunnerProperties.StubsMode.LOCAL,
		  ids = "it.vinicioflamini.omt:shipping-service:+:stubs")
public class ShippingServiceContractVerifierTest {

	private final String ENDPOINT_URL = "http://localhost:%d/";

	@Autowired 
	private StubFinder stubFinder;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	private RestTemplate restTemplate;
	
	private int port;

	@Before
	public void setup() {
		restTemplate = restTemplateBuilder.setConnectTimeout(Duration.ofMillis(5000))
				.setReadTimeout(Duration.ofMillis(5000)).build();
		
		port = stubFinder.findStubUrl("shipping-service").getPort();
	}

	@Test
	public void verifyPlacePaymentEndpointContract() {
		OrderRequest request = new OrderRequest();
		request.setOrderId(-1l);
		request.setItemId(-1l);
		request.setCustomerId(-1L);


		// Given:
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<OrderRequest> entity = new HttpEntity<>(request, headers);

		// When:
		ResponseEntity<String> response = restTemplate.exchange(String.format(ENDPOINT_URL, port), HttpMethod.POST, entity, String.class);

		// then:
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).matches("[\\S\\s]+");

	}

}