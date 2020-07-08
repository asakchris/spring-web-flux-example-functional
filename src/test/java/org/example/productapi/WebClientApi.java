package org.example.productapi;

import org.example.productapi.model.Product;
import org.example.productapi.model.ProductEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WebClientApi {
  private WebClient webClient;

  @BeforeEach
  void init() {
    webClient = WebClient.builder().baseUrl("http://localhost:8080/products").build();
  }

  private Mono<ResponseEntity<Product>> postNewProduct() {
    return webClient
        .post()
        .body(Mono.just(new Product(null, "Black Tea", 1.99)), Product.class)
        .exchange()
        .flatMap(response -> response.toEntity(Product.class))
        .doOnSuccess(response -> System.out.println("******POST: " + response));
  }

  private Flux<Product> getAllProducts() {
    return webClient
        .get()
        .retrieve()
        .bodyToFlux(Product.class)
        .doOnNext(product -> System.out.println("******GET: " + product));
  }

  private Mono<Product> updateProduct(String id, String name, double price) {
    return webClient
        .put()
        .uri("/{id}", id)
        .body(Mono.just(new Product(null, name, price)), Product.class)
        .retrieve()
        .bodyToMono(Product.class)
        .doOnSuccess(product -> System.out.println("******UPDATE: " + product));
  }

  private Mono<Void> deleteProduct(String id) {
    return webClient
        .delete()
        .uri("/{id}", id)
        .retrieve()
        .bodyToMono(Void.class)
        .doOnSuccess(obj -> System.out.println("******DELETE: " + obj));
  }

  private Flux<ProductEvent> getAllEvents() {
    return webClient.get().uri("/events").retrieve().bodyToFlux(ProductEvent.class);
  }

  @Test
  void test() {
    postNewProduct()
        .thenMany(getAllProducts())
        .take(1)
        .flatMap(product -> updateProduct(product.getId(), "White Tea", 0.99))
        .flatMap(product -> deleteProduct(product.getId()))
        .thenMany(getAllProducts())
        .thenMany(getAllEvents())
        .subscribe(System.out::println);
  }
}
