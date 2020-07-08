package org.example.productapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.example.productapi.model.Product;
import org.example.productapi.model.ProductEvent;
import org.example.productapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class TestAutoConfigureWebTestClient {
  @Autowired private WebTestClient client;

  private List<Product> expectedList;

  @Autowired private ProductRepository repository;

  @BeforeEach
  void beforeEach() {
    this.expectedList = repository.findAll().collectList().block();
    this.client = this.client.mutate().baseUrl("/products").build();
  }

  @Test
  void testGetAllProducts() {
    client
        .get()
        .uri("/")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(Product.class)
        .isEqualTo(expectedList);
  }

  @Test
  void testProductInvalidIdNotFound() {
    client.get().uri("/aaa").exchange().expectStatus().isNotFound();
  }

  @Test
  void testProductIdFound() {
    Product expectedProduct = expectedList.get(0);
    client
        .get()
        .uri("/{id}", expectedProduct.getId())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Product.class)
        .isEqualTo(expectedProduct);
  }

  @Test
  void testProductEvents() {
    FluxExchangeResult<ProductEvent> result =
        client
            .get()
            .uri("/events")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ProductEvent.class);

    ProductEvent expectedEvent = new ProductEvent(0L, "Product Event");

    StepVerifier.create(result.getResponseBody())
        .expectNext(expectedEvent)
        .expectNextCount(2)
        .consumeNextWith(event -> assertEquals(Long.valueOf(3), event.getEventId()))
        .thenCancel()
        .verify();
  }
}
