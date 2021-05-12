package it.vinicioflamini.omt.shipping.test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import it.vinicioflamini.omt.shipping.controller.ShippingController;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMessageVerifier
@DirtiesContext
public class BaseTestClass {

	@Autowired
	private ShippingController shippingController;

	@Before
	public void setup() {
		StandaloneMockMvcBuilder standaloneMockMvcBuilder = MockMvcBuilders.standaloneSetup(shippingController);
		RestAssuredMockMvc.standaloneSetup(standaloneMockMvcBuilder);
	}
}