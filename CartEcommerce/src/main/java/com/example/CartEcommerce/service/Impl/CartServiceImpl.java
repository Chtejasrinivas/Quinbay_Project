package com.example.CartEcommerce.service.Impl;

import com.example.CartEcommerce.dto.CartDto;
import com.example.CartEcommerce.dto.CartReturnDto;
import com.example.CartEcommerce.dto.CartStatus;
import com.example.CartEcommerce.dto.EmailService;
import com.example.CartEcommerce.entites.CartEntity;
import com.example.CartEcommerce.entites.ProductsEntity;
import com.example.CartEcommerce.feign.FeignInterface;
import com.example.CartEcommerce.feign.FeignInterfaceForEmail;
import com.example.CartEcommerce.repositories.CartRepo;
import com.example.CartEcommerce.service.CartService;
import com.example.CartEcommerce.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CartServiceImpl implements CartService {


    @Autowired
    CartRepo cartRepo;

    @Autowired
    OrderService orderService;

    @Autowired
    FeignInterface feignInterface;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    FeignInterfaceForEmail feignInterfaceForEmail;


    @Override
    public void addToCart() {

    }

    @Override
    public void addEntity(CartEntity cartEntity) {
        cartRepo.save(cartEntity);

    }


    public List<ProductsEntity> getProductDetailsBYProductId(String id) {

        List<ProductsEntity> l = new ArrayList<ProductsEntity>();
        List<ProductsEntity> productsIterable = feignInterface.getByProductId(id);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        for (ProductsEntity s : productsIterable) {

            l.add(s);
        }
        return l;
    }

    @Override
    public CartReturnDto getProductDetailsByUserId(String id)
    {

        Optional<CartEntity> optional = cartRepo.findById(id);
        CartReturnDto cartDto = new CartReturnDto();

        if (optional.isPresent())
        {
            BeanUtils.copyProperties(optional.get(), cartDto);
        }
        return cartDto;
    }

    @Override
    public void deleteBy(String userid, String productid) {

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(userid));
        Update update = new Update();
        update.pull("productsEntities",new Query(Criteria.where("productId").is(productid)));
        mongoTemplate.findAndModify(query,update,CartEntity.class);
    }

    @Override
    public CartStatus allAllToCart(String userid)
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(userid));
        List<CartEntity> cartEntityList = mongoTemplate.find(query,CartEntity.class);
        System.out.println(cartEntityList);
        if(cartEntityList.size()!=0) {
            orderService.AddingAllProductsToOrder(cartEntityList.get(0));
            cartRepo.deleteById(userid);
        }
        CartStatus cartStatus = new CartStatus();
        cartStatus.setStatus("Successfully Added to Orders !");
        cartStatus.setTotalCost(0);


        EmailService emailService = new EmailService();
        emailService.setRecipient(userid);
        emailService.setMsgBody("ordered Successfully");
        emailService.setSubject("Order Detail");
        String s = feignInterfaceForEmail.callEmailServer(emailService);
        return cartStatus;

    }

}