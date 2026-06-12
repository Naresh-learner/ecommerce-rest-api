package com.codewithmosh.store.controllers;


import com.codewithmosh.store.dtos.ProductDto;
import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.mappers.ProductMapper;
import com.codewithmosh.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@AllArgsConstructor

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    // we should map products to product dtos . for that we should create a mapper.
    // finally we expose this using getMapping annotation.
    @GetMapping
    public List<ProductDto>  getAllProducts(
            @RequestParam(name = "categoryId",required=false ) Byte categoryId
    ){
        List<Product> products ;
        if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);

        }
        else{
//            products = productRepository.findAll();
            products = productRepository.findAllWithCategory();
        }
        return products.stream().map(productMapper::toDto).toList();
        // This return s a stream. So we convert it to a Listand then return the list;
    }
}
