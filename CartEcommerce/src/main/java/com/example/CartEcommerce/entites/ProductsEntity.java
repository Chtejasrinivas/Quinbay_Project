package com.example.CartEcommerce.entites;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProductsEntity {

    private String productId;
    private String productName;
    private String brand;
    private String categoryId;
    private String productDescription;
    private String imageURL;
    private String merchantId;
    private String merchantName;
    private double price;
    private Integer stock;

}
