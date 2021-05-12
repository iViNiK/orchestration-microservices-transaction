package it.vinicioflamini.omt.inventory.test;

import it.vinicioflamini.omt.inventory.test.BaseTestClass;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.Rule;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import io.restassured.response.ResponseOptions;

import static org.springframework.cloud.contract.verifier.assertion.SpringCloudContractAssertions.assertThat;
import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.*;
import static com.toomuchcoding.jsonassert.JsonAssertion.assertThatJson;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;

@SuppressWarnings("rawtypes")
public class PackageTest extends BaseTestClass {

	@Test
	public void validate_inventoryControllerCompensateStub() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"orderId\":-1,\"itemId\":-1}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/compensate");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);

		// and:
			String responseBody = response.getBody().asString();
			assertThat(responseBody).isEqualTo("Request placed for item -1 compensation");
	}

	@Test
	public void validate_inventoryControllerDoInvontoryStub() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"orderId\":-1,\"itemId\":-1}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);

		// and:
			String responseBody = response.getBody().asString();
			assertThat(responseBody).isEqualTo("Request placed for item -1 fetching");
	}

}
