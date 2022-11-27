package iss.workshop.livestreamapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import iss.workshop.livestreamapp.interfaces.ISessionUser;
import iss.workshop.livestreamapp.models.LoginBag;
import iss.workshop.livestreamapp.models.User;
import iss.workshop.livestreamapp.services.LoginApi;
import iss.workshop.livestreamapp.services.RetroFitService;
import iss.workshop.livestreamapp.services.StreamsApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements ISessionUser {

    private Button btnLogin;
    TextView newAccount;
    private SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPage();
            }
        });
        newAccount = findViewById(R.id.createNewAccount);
        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        sPref = getSharedPreferences("userDetails", Context.MODE_PRIVATE);


        SharedPreferences.Editor editor = sPref.edit();
        editor
                .putString("username", "testUser")
                .putString("password", "password")
                .apply();


        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPage();
            }
        });
    }

    public void openPage (){
        TextView txtUsername = findViewById(R.id.txtUsername);
        String username = txtUsername.getText().toString();
        TextView txtPassword = findViewById(R.id.txtPassword);
        String password = txtPassword.getText().toString();

        RetroFitService rfServ = new RetroFitService("login");
        LoginApi loginAPI = rfServ.getRetrofit().create(LoginApi.class);
        LoginBag dummyUser = new LoginBag(username,password);

        loginAPI.checkUserIfExist(dummyUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.code() == 404){
                    Toast.makeText(LoginActivity.this, "Username and Password invalid. Please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    validate(response.body(), username, password);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Username and Password invalid. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void validate(User body, String username, String password) {
        Intent intent = new Intent(this, EntranceActivity.class);

        SharedPreferences.Editor editor = sPref.edit();
        editor
                .putString("username", username)
                .putString("password", password)
                .apply();

        body.setPassword(password);
        body.setUsername(username);
        intent.putExtra("user", body);

        startActivity(intent);
    }
}