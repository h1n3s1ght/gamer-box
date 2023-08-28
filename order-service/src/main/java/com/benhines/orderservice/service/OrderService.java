package com.benhines.orderservice.service;

import com.benhines.orderservice.dto.InventoryCount;
import com.benhines.orderservice.dto.InventoryResponse;
import com.benhines.orderservice.dto.OrderLineItemsDto;
import com.benhines.orderservice.dto.OrderRequest;
import com.benhines.orderservice.exceptions.NoProductStockException;
import com.benhines.orderservice.exceptions.NotEnoughStockException;
import com.benhines.orderservice.model.Order;
import com.benhines.orderservice.model.OrderLineItem;
import com.benhines.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepo;
    private final WebClient.Builder webClientBuilder;


    public void placeOrder(OrderRequest orderRequest) throws NotEnoughStockException, NoProductStockException {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemsDto()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItems(orderLineItems);
        List<String> orderSkuCodes = order.getOrderLineItems().stream()
                .map(OrderLineItem::getSkuCode).toList();

        //-- Get Inventory Info for each SKU --
        InventoryResponse[] inventoryResponse = checkInStockInventory(orderSkuCodes);

        //-- Check inStock --
        List<String> outOfStockItems = checkIfInStock(inventoryResponse);

        //-- Quantity requested in Stock? --
        List<InventoryCount> quantityNotAvailable = checkStockQuantity(inventoryResponse, orderLineItems);


        //-- Set Results --
        if(!outOfStockItems.isEmpty()){
            throw new NoProductStockException("\n\nThe following item(s) can not be sold at this time as they are not in stock: \n---   " + outOfStockItems + "   ---\n");
        } else if (!quantityNotAvailable.isEmpty()) {
            throw new NotEnoughStockException("\n\nThe following item(s) can not be sold at this time as we do not have this quantity in stock.  See item(s) list below to see how many you can request: \n---   " + quantityNotAvailable + "   ---\n");
        } else {
            orderRepo.save(order);
            log.info("\n\n---   Order created successfully.   ---\n");
        }
    }



    private InventoryResponse[] checkInStockInventory(List<String> skuCodes) {
        return webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes)
                                .build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
    }


    private List<String> checkIfInStock(InventoryResponse[] request) {
        List<String> results = new ArrayList<>();
        for (InventoryResponse response : request) {
            if (!response.getInStock()){
                results.add(response.getSkuCode());
            }
        }
        return results;
    }

    private List<InventoryCount> checkStockQuantity(InventoryResponse[] inventoryResponses, List<OrderLineItem> orderLineItems) {
        List<InventoryCount> results = new ArrayList<>();

        for (OrderLineItem orderLineItem : orderLineItems) {
            String skuCode = orderLineItem.getSkuCode();
            InventoryResponse inventoryResponse = findInventoryResponse(inventoryResponses, skuCode);

            if (inventoryResponse != null && orderLineItem.getQuantity() > inventoryResponse.getActiveCount()) {
                Integer availableQuantity = inventoryResponse.getActiveCount();
                results.add(InventoryCount.builder()
                        .skuCode(skuCode)
                        .availableQuantity(availableQuantity)
                        .build());
            }
        }
        return results;
    }


    private InventoryResponse findInventoryResponse(InventoryResponse[] inventoryResponses, String skuCode) {
        for (InventoryResponse response : inventoryResponses) {
            if (response.getSkuCode().equals(skuCode)) {
                return response;
            }
        }
        return null;
    }



    private OrderLineItem mapToDto(OrderLineItemsDto orderLineItemsDto){
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemsDto.getPrice());
        orderLineItem.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItem;
    }
}
