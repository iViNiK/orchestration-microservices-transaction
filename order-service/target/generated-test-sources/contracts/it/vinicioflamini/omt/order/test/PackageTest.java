package it.vinicioflamini.omt.order.test;

import it.vinicioflamini.omt.order.test.BaseTestClass;
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
	public void validate_orderControllerCompensateStub() throws Exception {
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
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['message']").matches("[\\S\\s]+");
			assertThatJson(parsedJson).field("['orderId']").isEqualTo(-1);
	}

	@Test
	public void validate_orderControllerPlaceOrderStub() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"itemId\":-1,\"customerId\":-1}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['message']").matches("[\\S\\s]+");
			assertThatJson(parsedJson).field("['orderId']").matches("-?(\\d*\\.\\d+|\\d+)");
			assertThatJson(parsedJson).field("['itemId']").isEqualTo(-1);
			assertThatJson(parsedJson).field("['customerId']").isEqualTo(-1);
	}

}
