package com.example.CartEcommerce.feign;


import com.example.CartEcommerce.entites.ProductsEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "productsMongo", url = "http://10.20.2.120:8095/products/")
public interface FeignInterface {

    @PostMapping(value = "getByProductId/{id}")
    List<ProductsEntity> getByProductId(@PathVariable("id") String id);




}
