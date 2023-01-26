package com.example.CartEcommerce.service;

import com.example.CartEcommerce.dto.CartReturnDto;
import com.example.CartEcommerce.entites.CartEntity;
import com.example.CartEcommerce.entites.ProductsEntity;

import java.util.List;

public interface CartService {

    public void addToCart();
    public void addEntity(CartEntity cartEntity);
    public List<ProductsEntity> getProductDetailsBYProductId(String id);

    public CartReturnDto getProductDetailsByUserId(String id);
    public void deleteBy(String userid,String productid);
}
