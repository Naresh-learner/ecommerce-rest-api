package com.codewithmosh.store.dtos;

import lombok.Data;

import java.math.BigDecimal;
@Data//instead of getters and setters
public class ProductDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private Byte categoryId;

}
