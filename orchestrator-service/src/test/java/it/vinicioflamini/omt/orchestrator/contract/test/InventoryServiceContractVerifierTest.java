package it.vinicioflamini.omt.orchestrator.contract.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext
@AutoConfigureStubRunner(
		  stubsMode = StubRunnerProperties.StubsMode.LOCAL,
		  ids = "it.vinicioflamini.omt:inventory-service:+:stubs:8888")
public class InventoryServiceContractVerifierTest {

	private final String ENDPOINT_URL = "http://localhost:8888/";

	private final String RESPONSE_DO_INVENTORY = "Request placed for item -1 fetching";
	
	private final String RESPONSE_COMPENSATE = "Request placed for item -1 compensation";

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	private RestTemplate restTemplate;

	@Before
	public void setup() {
		restTemplate = restTemplateBuilder.setConnectTimeout(Duration.ofMillis(5000))
				.setReadTimeout(Duration.ofMillis(5000)).build();
	}

	@Test
	public void verifyDoInventoryEndpointContract() {
		String request = "{\"orderId\": -1, \"itemId\": -1}";

		// Given:
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<>(request, headers);

		// When:
		ResponseEntity<String> response = restTemplate.exchange(ENDPOINT_URL, HttpMethod.POST, entity, String.class);

		// then:
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(RESPONSE_DO_INVENTORY);

	}

	@Test
	public void verifyCompensateEndpointContract() {
		String request = "{\"orderId\": -1, \"itemId\": -1}";

		// Given:
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<>(request, headers);

		// When:
		ResponseEntity<String> response = restTemplate.exchange(ENDPOINT_URL + "/compensate", HttpMethod.POST, entity, String.class);

		// then:
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(RESPONSE_COMPENSATE);

	}

}