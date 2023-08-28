package com.benhines.productservice.repository;

import com.benhines.productservice.dto.ProductResponse;
import com.benhines.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface ProductRepo extends MongoRepository<Product, String> {

    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    ProductResponse getProductByName(String name);

    @Query("{'_id': ?0}")
    ProductResponse getProductById(String id);

    void deleteById(String id);

}