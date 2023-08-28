package com.benhines.orderservice.controller;

import com.benhines.orderservice.dto.OrderRequest;
import com.benhines.orderservice.exceptions.NoProductStockException;
import com.benhines.orderservice.exceptions.NotEnoughStockException;
import com.benhines.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<String> placeNewOrder(@RequestBody OrderRequest orderRequest){
        try {
            service.placeOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Order Placed Successfully.");
        } catch (NoProductStockException e) {
            log.info("\n\n---   Order contains item(s) listed as 'out of stock.'   ---\n");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getMessage());
        } catch (NotEnoughStockException e) {
            log.info("\n\n---   Order contains of item(s) requested with a quantity not available.   ---\n");
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getMessage());
        }
    }
}
