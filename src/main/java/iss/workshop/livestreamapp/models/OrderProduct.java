package iss.workshop.livestreamapp.models;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderProduct implements Serializable  {
    private String id;
    private int quantity;
    private Orders order;
    private Product product;
    private ChannelStream channel;
    private Cart cart;

    public OrderProduct(int quantity) {
        this.quantity = quantity;
    }

    public OrderProduct(int quantity, Product product, Orders order, ChannelStream channel, Cart cart) {
        this.quantity = quantity;
        this.product = product;
        this.channel = channel;
        this.cart = cart;
        this.order = order;
    }
}
