
package com.example.order_service;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final RestClient restClient;

    // Khoi tao RestClient truc tiep
    public OrderController() {
        this.restClient = RestClient.create("http://localhost:8081");
    }

    // API lay danh sach don hang
    @GetMapping
    public List<Map<String, Object>> getOrders() {
        return List.of(
            Map.of("id", 101, "productId", 1, "quantity", 2),
            Map.of("id", 102, "productId", 2, "quantity", 1)
        );
    }

    // API goi Product Service de tim san pham
    @GetMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> getProduct(
            @PathVariable int id) {

        if (id < 1) {
            return ResponseEntity.badRequest()
                .body(Map.of("error",
                    "Vui long nhap ID san pham hop le."));
        }

        try {
            Map<String, Object> product = restClient.get()
                .uri("/api/products/{id}", id)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (product == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(product);

        } catch (HttpClientErrorException.NotFound ex) {
            return ResponseEntity.notFound().build();

        } catch (RestClientException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error",
                    "Khong the ket noi den Product Service (cong 8081)."));
        }
    }
}
