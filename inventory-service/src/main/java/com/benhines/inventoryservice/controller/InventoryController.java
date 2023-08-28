package com.benhines.inventoryservice.controller;


import com.benhines.inventoryservice.dto.InventoryResponse;
import com.benhines.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Slf4j
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode){
        log.info("\n\n---   Checked stock of: "+ skuCode + "   ---\n");
        return service.isInStock(skuCode);
    }
}
