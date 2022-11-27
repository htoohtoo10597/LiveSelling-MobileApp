package iss.workshop.livestreamapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.User;
import iss.workshop.livestreamapp.services.ChannelsApi;
import iss.workshop.livestreamapp.services.RetroFitService;
import iss.workshop.livestreamapp.services.UserApi;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private Button btnBack;
    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private EditText txtChannelName;
    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtAddress;
    private CheckBox ckbxVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);
        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);
        txtConfirmPassword = findViewById(R.id.confirm_password);
        //txtChannelName = findViewById(R.id.channel_name);
        txtAddress = findViewById(R.id.address);
        txtFirstName = findViewById(R.id.first_name);
        txtLastName = findViewById(R.id.last_name);
        ckbxVerify = findViewById(R.id.checkbox_verify);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View view) {
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                String confirm = txtConfirmPassword.getText().toString();
                //String channelName = txtChannelName.getText().toString();
                String address = txtAddress.getText().toString();
                String firstName = txtFirstName.getText().toString();
                String lastName = txtLastName.getText().toString();
                boolean isVerified = ckbxVerify.isChecked();

                if(!password.equals(confirm)){
                    //not confirmed
                    Toast.makeText(RegisterActivity.this,
                            "Please make sure the Password and the Confirm Password fields match.",
                            Toast.LENGTH_SHORT).show();

                } else if (username.isEmpty() || password.isEmpty() || confirm.isEmpty() ||
                        firstName.isEmpty() || lastName.isEmpty() || address.isEmpty()){
                    //empty fields
                    Toast.makeText(RegisterActivity.this, "Submit complete form, and try again", Toast.LENGTH_SHORT).show();
                } else {
                    //confirmed
                    User user = new User(firstName, lastName, address, username, password, isVerified);
                    System.out.println(user.getUsername() + " " + user.getPassword());
                    RetroFitService rfServ = new RetroFitService("save-user");
                    UserApi userAPI = rfServ.getRetrofit().create(UserApi.class);
                    userAPI.addNewUser(user, username, password, address, firstName + "'s Channel").enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if(response.code() == 409){
                                Toast.makeText(RegisterActivity.this, "A username already exists with that username. Please try again.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Thank you for registering! You may now log in using your credentials.", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }
                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(RegisterActivity.this, "Registration unsuccessful. Try again in a few minutes.", Toast.LENGTH_SHORT).show();
                        }
                    });


                    //run api to save user
                    //run api to save channel
                }

            }
        });
    }
}