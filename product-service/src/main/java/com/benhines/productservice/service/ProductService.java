package com.benhines.productservice.service;


import com.benhines.productservice.dto.ProductRequest;
import com.benhines.productservice.dto.ProductResponse;
import com.benhines.productservice.exception.NoChangeException;
import com.benhines.productservice.exception.ProductNotFoundException;
import com.benhines.productservice.model.Product;
import com.benhines.productservice.repository.ProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ProductService {

    //-- Initialize Product Repository --
    //-----------------------------------
    private final ProductRepo productRepo;
    private final MongoTemplate mongoTemplate;


    //-- Autowired --
    //---------------
    @Autowired
    public ProductService(ProductRepo productRepo, MongoTemplate mongoTemplate) {
        this.productRepo = productRepo;
        this.mongoTemplate = mongoTemplate;
    }


    //== CREATE PRODUCT ==
    //--------------------
    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        productRepo.save(product);

        //-- Log Results --
        log.info("\n\nProduct {} is created successfully.", product.getId());
        log.trace("\nProduct {} is created containing: \n{}\n", product.getId(), product);
    }

    //== GET ALL PRODUCTS ==
    //----------------------
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepo.findAll();

        //-- Log Results --
        log.info("\n\n---   Retrieved all products. Total: {}   ---\n", products.size());

        //-- Return List --
        return products.stream().map(this::mapToProductResponse).toList();

    }


    //== GET PRODUCT BY NAME ==
    //-------------------------
    public ProductResponse getProductByName(String name) {

        //-- Log Results --
        log.info("\n\n---   Found Product with name that contains: {} \n---   Response: {}   ---\n", name, productRepo.getProductByName(name));

        return productRepo.getProductByName(name);
    }


    //== GET PRODUCT BY ID ==
    //-------------------------
    public ProductResponse getProductById(String id) {

        //-- Log Results --
        log.info("\n\n---   Found Product with ID: {} \n---   Response: {}   ---\n", id, productRepo.getProductById(id));

        return productRepo.getProductById(id);
    }


    //== UPDATE PRODUCT BY ID ==
    //--------------------------
    public void updateProductById(String id, Product product) {

        //-- Verify Changes --
        ProductResponse current = productRepo.getProductById(id);
        if (current == null) {throw new ProductNotFoundException();}
        if (!hasProductChanged(current, product)) {throw new NoChangeException();}

        //-- Query + Update --
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update()
                .set("name", product.getName())
                .set("description", product.getDescription())
                .set("price", product.getPrice());

        mongoTemplate.findAndModify(query, update, Product.class);

        //-- Log Results --
        log.info("\n\n---   Product: {} ... was updated successfully.   ---\n", product.getName());
    }

    //== DELETE PRODUCT BY ID ==
    //--------------------------
    public void deleteProductById(String id) {
        if (productRepo.getProductById(id) == null){
            throw new ProductNotFoundException();
        } else {
            productRepo.deleteById(id);

            //-- Log Results --
            log.info("\n\n---  Product: {} ... was deleted successfully.   ---\n", id);
        }
    }




    //-- Methods --
    //-------------

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    private boolean hasProductChanged(ProductResponse current, Product product) {
        return !Objects.equals(current.getName(), product.getName())
                || !Objects.equals(current.getDescription(), product.getDescription())
                || !Objects.equals(current.getPrice(), product.getPrice());
    }

}
