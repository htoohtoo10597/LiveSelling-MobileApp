package iss.workshop.livestreamapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Cart implements Serializable {

    private String id;
    private User user;

    private List<OrderProduct> orderProduct;

    public Cart(String id, User user) {
        this.id = id;
        this.orderProduct = new ArrayList<>();
        this.user = user;
    }
}
