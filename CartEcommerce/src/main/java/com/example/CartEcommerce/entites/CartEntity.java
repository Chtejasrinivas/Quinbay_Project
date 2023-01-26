package com.example.CartEcommerce.entites;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document
public class CartEntity {

    @Id
    private String userId;

    private List<ProductsEntity> productsEntities;


}
