package iss.workshop.livestreamapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.nio.channels.Channel;
import java.util.List;
import java.util.stream.Collectors;

import iss.workshop.livestreamapp.interfaces.IMenuAccess;
import iss.workshop.livestreamapp.interfaces.IStreamDetails;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.Rating;
import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.User;
import iss.workshop.livestreamapp.services.ChannelsApi;
import iss.workshop.livestreamapp.services.DashboardApi;
import iss.workshop.livestreamapp.services.RetroFitService;
import iss.workshop.livestreamapp.services.UserApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelProfileActivity extends AppCompatActivity implements IMenuAccess, IStreamDetails {

    private User user;
    private ChannelStream channelOfUser;
    private ChannelStream channelToView;
    private Stream currStream;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private String action;
    private RatingBar ratingBar;
    private Button btnSubmit;
    private Button btnVerify;
    private TextView rateCount, showRating;
    private TextView channelName;
    private String ratingToDisplay;
    EditText review;
    float rateValue; String temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_profile);

        //get user
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        //get channel

        if(intent.getSerializableExtra("channel") == null){
            //find the channel here
            RetroFitService rfServ = new RetroFitService("get-channel-from-id");
            ChannelsApi channelAPI = rfServ.getRetrofit().create(ChannelsApi.class);

            channelAPI.findChannelByUserId(user.getId()).enqueue(new Callback<ChannelStream>() {
                @Override
                public void onResponse(Call<ChannelStream> call, Response<ChannelStream> response) {
                    channelOfUser = response.body();
                }

                @Override
                public void onFailure(Call<ChannelStream> call, Throwable t) {
                    Toast.makeText(ChannelProfileActivity.this, "Channel for user not found.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            channelOfUser = (ChannelStream) intent.getSerializableExtra("channel");
        }

        action = intent.getAction();

        if (action.equals("view-as-own")){
            channelToView = channelOfUser;
        } else {
            channelToView = (ChannelStream) intent.getSerializableExtra("channel-to-view");
        }

        //rating bar
        channelName = findViewById(R.id.channel_name);
        channelName.setText(channelToView.getName());

        TextView channelUsername = findViewById(R.id.role_name);
        channelUsername.setText(channelToView.getUser().getFirstName() + " " + channelToView.getUser().getLastName());

        TextView avgRating = findViewById(R.id.rating);

        RetroFitService rfServ = new RetroFitService("stream");
        DashboardApi dashboardAPI = rfServ.getRetrofit().create(DashboardApi.class);
        dashboardAPI.getUserAverageRating(channelToView.getUser().getId()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                ratingToDisplay = response.body();
                avgRating.setText("AVERAGE RATING: " + ratingToDisplay + "/5");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        btnVerify = findViewById(R.id.verify_account);
        btnVerify.setVisibility(View.GONE);

        rateCount = findViewById(R.id.rate_Count);
        ratingBar = findViewById(R.id.rating_bar);
        btnSubmit = findViewById(R.id.rating_submit);

        //review = findViewById(R.id.write_Review);

        showRating = findViewById(R.id.showRating);

        if(action.equals("view-as-other")){
            //if seeing from perspective of other user
            channelToView = (ChannelStream) intent.getSerializableExtra("channel-to-view");
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    rateValue = ratingBar.getRating();

                    if(rateValue <=1 && rateValue>0)
                        rateCount.setText("Bad " + rateValue + "/5");
                    else if(rateValue <=2 && rateValue>1)
                        rateCount.setText("Ok " + rateValue + "/5");
                    else if(rateValue <=3 && rateValue>2)
                        rateCount.setText("Good " + rateValue + "/5");
                    else if(rateValue <=4 && rateValue>3)
                        rateCount.setText("Very Good " + rateValue + "/5");
                    else if(rateValue <=5 && rateValue>4)
                        rateCount.setText("Best " + rateValue + "/5");
                }
            });

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int rate = (int) ratingBar.getRating();
                    Rating rating = new Rating();
                    rating.setRate(rate);

                    RetroFitService rfServ = new RetroFitService("rating");
                    ChannelsApi channelAPI = rfServ.getRetrofit().create(ChannelsApi.class);

                    channelAPI.saveRating(rating, channelToView.getId(), user.getId()).enqueue(new Callback<Rating>() {
                        @Override
                        public void onResponse(Call<Rating> call, Response<Rating> response) {
                            Toast.makeText(ChannelProfileActivity.this, "Sent a rating of " + rate + " to " +
                                    channelToView.getUser().getFirstName() + " " + channelToView.getUser().getLastName(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Rating> call, Throwable t) {
                            Toast.makeText(ChannelProfileActivity.this, "Not able to send rating.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    btnSubmit.setEnabled(false);
                    temp = rateCount.getText().toString();
                    showRating.setText("Your Rating: \n"+ temp+ "\n");
                    ratingBar.setRating(0);
                    rateCount.setText("");
                }
            });
        } else {
            //if looking from own perspective
            //verify button
            if(!user.getIsVerified()){
                btnVerify.setVisibility(View.VISIBLE);
                btnVerify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RetroFitService rfServ = new RetroFitService("verify-user");
                        UserApi userAPI = rfServ.getRetrofit().create(UserApi.class);
                        userAPI.verifyUser(user.getId()).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                user.setIsVerified(true);
                                Toast.makeText(ChannelProfileActivity.this, "You are now verified!", Toast.LENGTH_SHORT).show();
                                //refresh on load
                                startActivity(getIntent());
                                finish();
                                overridePendingTransition(0, 0);
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {

                            }
                        });
                    }
                });
            }
            LinearLayout ratingPanel = findViewById(R.id.rating_panel);
            ratingPanel.setVisibility(View.INVISIBLE);

        }

        setupSidebarMenu();


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //make nav clickable
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        plantOnClickItems(this, item, user, channelOfUser);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setupSidebarMenu() {
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (!user.getIsVerified()){
            MenuItem streamsnav = navigationView.getMenu().findItem(R.id.nav_streams);
            streamsnav.setVisible(false);

            MenuItem productsnav = navigationView.getMenu().findItem(R.id.nav_products);
            productsnav.setVisible(false);

            MenuItem ordersnav = navigationView.getMenu().findItem(R.id.nav_orders);
            ordersnav.setVisible(false);

            MenuItem dashboardnav = navigationView.getMenu().findItem(R.id.nav_dashboard);
            dashboardnav.setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(this);


        navigationView.setNavigationItemSelectedListener(this);
    }

}