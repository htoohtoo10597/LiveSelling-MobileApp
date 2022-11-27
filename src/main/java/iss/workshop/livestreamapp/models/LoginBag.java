package iss.workshop.livestreamapp.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginBag {
    private String username;
    private String password;


    public LoginBag(String username, String password){
        this.username = username;
        this.password = password;
    }
}
