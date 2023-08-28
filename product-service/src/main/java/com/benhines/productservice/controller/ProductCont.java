package com.benhines.productservice.controller;


import com.benhines.productservice.dto.ProductRequest;
import com.benhines.productservice.dto.ProductResponse;
import com.benhines.productservice.model.Product;
import com.benhines.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductCont{

    //-- Initialize Product Service --
    //--------------------------------
    private final ProductService productService;


    //== CREATE PRODUCT || POST ==
    //----------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest){ productService.createProduct(productRequest); }


    //== GET ALL PRODUCTS || GET ==
    //-----------------------------
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts(){
        return productService.getAllProducts();
    }


    //== GET PRODUCT BY NAME || GET ==
    //--------------------------------
    @GetMapping("/name/{productName}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProductByName(@PathVariable("productName") String productName){return productService.getProductByName(productName);}


    //== GET PRODUCT BY ID || GET ==
    //------------------------------
    @GetMapping("/id/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProductById(@PathVariable("productId") String id){return productService.getProductById(id);}


    //== UPDATE PRODUCT || PUT ==
    //---------------------------
    @PutMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProduct(@PathVariable("productId") String id, @RequestBody Product product){productService.updateProductById(id, product);}


    //== DELETE PRODUCT BY ID || DELETE ==
    //------------------------------------
    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProduct(@PathVariable("productId") String productId){productService.deleteProductById(productId);}

}
