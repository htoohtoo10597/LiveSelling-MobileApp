package iss.workshop.livestreamapp.models;

import androidx.navigation.NavType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
public class User implements Serializable {

    private String id;
    private String firstName;
    private String lastName;
    private String address;
    private String username;
    private String password;
    private Boolean isVerified;
    private Cart cart;
    private ChannelStream channel;

    private List<Rating> reviews;
    private List<Orders> ordersHistory;

    public User(String firstName, String lastName, String address, String username, String password, Boolean isVerified) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.username = username;
        this.password = password;
        this.isVerified = isVerified;
        this.reviews = new ArrayList<>();
        this.ordersHistory = new ArrayList<>();
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

}
