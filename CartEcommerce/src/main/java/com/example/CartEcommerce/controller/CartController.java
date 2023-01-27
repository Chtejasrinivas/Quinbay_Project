package com.example.CartEcommerce.controller;


import com.example.CartEcommerce.dto.CartDto;
import com.example.CartEcommerce.dto.CartReturnDto;
import com.example.CartEcommerce.dto.CartStatus;
import com.example.CartEcommerce.entites.CartEntity;
import com.example.CartEcommerce.entites.ProductsEntity;
import com.example.CartEcommerce.feign.FeignInterface;
import com.example.CartEcommerce.repositories.CartRepo;
import com.example.CartEcommerce.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")

public class CartController {



    @Autowired
    CartService cartService;

    @Autowired
    CartRepo cartRepo;

    @Autowired
    FeignInterface feignInterface;
    @Autowired
    MongoTemplate mongoTemplate;



    @PostMapping (value = "/add")
    public ResponseEntity<CartStatus> addProducts(@RequestBody CartDto cartDto) {

       CartEntity cartEntity=new CartEntity();
        List<ProductsEntity> productsEntityList = cartService.getProductDetailsBYProductId(cartDto.getProductId());

        if(!cartRepo.existsById(cartDto.getUserId())){
             cartEntity.setUserId(cartDto.getUserId());
             cartEntity.setProductsEntities(productsEntityList);
             cartEntity.setTotalCost(productsEntityList.get(0).getPrice());
             cartService.addEntity(cartEntity);
        }
        else{

            Query query1 = new Query();
            query1.addCriteria(Criteria.where("_id").is(cartDto.getUserId()));

            List<CartEntity> orderEntities = mongoTemplate.find(query1,CartEntity.class);
            double price = orderEntities.get(0).getTotalCost() + productsEntityList.get(0).getPrice();

            Update update = new Update();
            update.set("totalCost",price);
            mongoTemplate.findAndModify(query1,update,CartEntity.class);

            update = new Update();
            update.addToSet("productsEntities",productsEntityList.get(0));

            mongoTemplate.findAndModify(query1,update,CartEntity.class);

        }

        CartStatus cartStatus = new CartStatus();
        cartStatus.setStatus("cart is added  with id : " + cartDto.getProductId());
        return new ResponseEntity(cartStatus, HttpStatus.CREATED);
    }





    @GetMapping (value = "/CartProduct/{id}")
    public ResponseEntity<List<ProductsEntity>> CartProducts(@PathVariable ("id") String id)
    {
        return new ResponseEntity(cartService.getProductDetailsBYProductId(id),HttpStatus.OK);
    }


    @GetMapping (value = "CartProductsById/{id}")
    public ResponseEntity<CartReturnDto> CartProductsById(@PathVariable ("id") String id)
    {
       return new ResponseEntity(cartService.getProductDetailsByUserId(id),HttpStatus.OK);

    }

    @DeleteMapping(value = "deleteCartProduct/{userId}/{productId}")
    public ResponseEntity<CartStatus> deleteCartProduct(@PathVariable ("userId") String userid, @PathVariable ("productId") String productid)
    {
        List<ProductsEntity> productsEntityList = cartService.getProductDetailsBYProductId(productid);

        Query query1 = new Query();
        query1.addCriteria(Criteria.where("_id").is(userid));
        List<CartEntity> orderEntities = mongoTemplate.find(query1,CartEntity.class);
        double price = orderEntities.get(0).getTotalCost() - productsEntityList.get(0).getPrice();
        Update update = new Update();
        update.set("totalCost",price);
        mongoTemplate.findAndModify(query1,update,CartEntity.class);
        cartService.deleteBy(userid,productid);
        CartStatus cartStatus = new CartStatus();
        cartStatus.setStatus("Product delted");
        cartStatus.setTotalCost(price);
        return  new ResponseEntity(cartStatus,HttpStatus.OK);
    }

    @PostMapping(value = "/addAllToOrder/{userId}")
    public ResponseEntity<CartStatus> addAllToOrder (@PathVariable("userId") String userId)
    {

        return  new ResponseEntity(cartService.allAllToCart(userId),HttpStatus.OK);
    }






//    @GetMapping(value = "increaseProduct/{userId}/{productId}")



}
