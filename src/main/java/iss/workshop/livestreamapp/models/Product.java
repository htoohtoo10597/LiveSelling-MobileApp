package iss.workshop.livestreamapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import iss.workshop.livestreamapp.helpers.ProductCategories;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Product implements Serializable {

    private String id;
    private String name;
    private ProductCategories category;
    private String description;
    private double price;
    private int quantity;

    private ChannelStream channel;
    private List<OrderProduct> orderProduct;

    public Product(String name, ProductCategories category, String description, double price, int quantity, ChannelStream channel) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.channel = channel;
        this.orderProduct = new ArrayList<>();
    }

}
