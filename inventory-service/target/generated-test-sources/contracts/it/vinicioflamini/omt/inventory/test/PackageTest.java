package it.vinicioflamini.omt.inventory.test;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import io.restassured.response.ResponseOptions;
import it.vinicioflamini.omt.inventory.test.BaseTestClass;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import static com.toomuchcoding.jsonassert.JsonAssertion.assertThatJson;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.springframework.cloud.contract.verifier.assertion.SpringCloudContractAssertions.assertThat;
import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.*;

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
			String responseBody = response.getBody().asString();
			assertThat(responseBody).isEqualTo("Request placed for item -1 compensation");
	}

	@Test
	public void validate_orderControllerDoInvontoryStub() throws Exception {
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
