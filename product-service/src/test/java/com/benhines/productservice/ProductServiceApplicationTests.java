package com.benhines.productservice;

import com.benhines.productservice.dto.ProductRequest;
import com.benhines.productservice.dto.ProductResponse;
import com.benhines.productservice.repository.ProductRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10");


	//---------------
	//== AUTOWIRED ==
	//---------------

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ProductRepo repo;


	//----------------------
	//== Dynamic Registry ==
	//----------------------

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry propertyRegistry) {
		propertyRegistry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl());
	}


	//-----------
	//== TESTS ==
	//-----------



	//== PRODUCTS ==
	//--------------


	//-- Create Product --
	@Test
	void createProduct() throws Exception {

		//-- Clear out Database --
		clearData();

		//-- Variables --
		ProductRequest productRequest = getProductRequest();
		String productRequestString = mapper.writeValueAsString(productRequest);

		//-- Add Product --
		mvc.perform(MockMvcRequestBuilders.post("/api/product")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productRequestString))
				.andExpect(status().isCreated());

		//-- Verify New Product --
		Assertions.assertEquals(1, repo.findAll().size());
	}


	//-- Get All Products --
	@Test
	void getProductTest() throws Exception {

		//-- GET Request to Endpoint --
		ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/api/product")
				.contentType(MediaType.APPLICATION_JSON));

		//-- Check Status Code --
		resultActions.andExpect(status().isOk());

		//-- Response Content --
		MvcResult result = resultActions.andReturn();
		String responseContent = result.getResponse().getContentAsString();

		//-- Deserialize JSON List --
		List<ProductResponse> products = mapper.readValue(responseContent, new TypeReference<List<ProductResponse>>() {});


		//== Verify Response ==
		//---------------------

			//-- Response Code --
			resultActions.andExpect(status().isOk());

			//-- Type Of ProductResponse --
			resultActions.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.anyOf(Matchers.isA(ProductResponse.class), Matchers.isA(List.class))));

			//-- # of products --
			if (products.size() >= 1) {
				// Verify that the response contains exactly 1 product
				Assertions.assertEquals(1, products.size());
			} else {
				// Log the message if there were no products retrieved
				System.out.println("There were no products retrieved.");
			}
	}

	//-------------
	//== Methods ==
	//-------------
	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("Test Product")
				.description("Product is not really available it's a test, silly!")
				.price(BigDecimal.valueOf(0))
				.build();
	}

	void clearData() {
		repo.deleteAll();
	}
}
