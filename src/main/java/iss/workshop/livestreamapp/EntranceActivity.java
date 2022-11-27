package iss.workshop.livestreamapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.stream.Collectors;

import io.agora.rtc2.Constants;
import iss.workshop.livestreamapp.adapters.ChStreamAdapter;
import iss.workshop.livestreamapp.interfaces.IMenuAccess;
import iss.workshop.livestreamapp.interfaces.ISessionUser;
import iss.workshop.livestreamapp.interfaces.IStreamDetails;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.User;
import iss.workshop.livestreamapp.services.ChannelsApi;
import iss.workshop.livestreamapp.services.RetroFitService;
import iss.workshop.livestreamapp.services.StreamsApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntranceActivity extends AppCompatActivity implements IStreamDetails, IMenuAccess, ISessionUser {

    private ListView listOfStreams;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ChannelStream channelStream;
    private Stream currStream;
    private TextView welcomeUser;
    private User user;
    private Button searchForStreams;
    private TextView searchTermBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        //Button startA = findViewById(R.id.startA);
        //Button startB = findViewById(R.id.startB);

        //checkUser method
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        if(intent.getSerializableExtra("channel") == null){
            //find the channel here
            RetroFitService rfServ = new RetroFitService("get-channel-from-id");
            ChannelsApi channelAPI = rfServ.getRetrofit().create(ChannelsApi.class);

            channelAPI.findChannelByUserId(user.getId()).enqueue(new Callback<ChannelStream>() {
                @Override
                public void onResponse(Call<ChannelStream> call, Response<ChannelStream> response) {
                    channelStream = response.body();
                }

                @Override
                public void onFailure(Call<ChannelStream> call, Throwable t) {
                    Toast.makeText(EntranceActivity.this, "Channel for user not found.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            channelStream = (ChannelStream) intent.getSerializableExtra("channel");
        }


        SharedPreferences sPref = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        if (isValidated(sPref, user.getUsername(), user.getPassword())){
            welcomeUser = findViewById(R.id.welcome_user);
            welcomeUser.setText("Welcome " + user.getFirstName() + " to the Live Shopping App!");
        } else {
            logOut(sPref, this);
        }

        setupSidebarMenu();

        //populating streams
        listOfStreams = findViewById(R.id.stream_list_first);

        RetroFitService rfServ = new RetroFitService("stream");
        StreamsApi streamAPI = rfServ.getRetrofit().create(StreamsApi.class);

        streamAPI.getAllNotCompletedStreams().enqueue(new Callback<List<Stream>>() {
            @Override
            public void onResponse(Call<List<Stream>> call, Response<List<Stream>> response)
            {
                populateStreamList(response.body());
            }

            @Override
            public void onFailure(Call<List<Stream>> call, Throwable t) {
                Toast.makeText(EntranceActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        searchForStreams = findViewById(R.id.search_layout)
                .findViewById(R.id.search);
        searchForStreams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTermBox = findViewById(R.id.search_term);
                String searchTerm = searchTermBox.getText().toString();
                Toast.makeText(EntranceActivity.this, "see this", Toast.LENGTH_SHORT).show();
                openSearchResults(searchTerm);

            }
        });
    }

    private void openSearchResults(String searchTerm) {
        if (searchTerm.length() < 3){
            Toast.makeText(this, "Your search term is too short. Please put a more relevant search term.", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, SearchResultsActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("channel", channelStream);
            intent.putExtra("searchTerm", searchTerm);
            startActivity(intent);
        }
    }

    private void populateStreamList(List<Stream> body) {
        ChStreamAdapter streamAdapter = new ChStreamAdapter(this, body, false, user);
        listOfStreams.setAdapter(streamAdapter);
        listOfStreams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                TextView channelNameTxt = view
                        .findViewById(R.id.entire_row)
                        .findViewById(R.id.top_container)
                        .findViewById(R.id.text_fields)
                        .findViewById(R.id.channel_name);
                String channel = channelNameTxt.getText().toString();

                currStream = (Stream) streamAdapter.getItem(i);
                if (currStream.getChannelStream().getUser().getId().equals(user.getId())){
                    Toast.makeText(EntranceActivity.this, "This is your stream! Start your stream in your My Streams Page.", Toast.LENGTH_SHORT).show();
                } else {
                    invokeToken(currStream.getChannelStream());
                    Toast.makeText(EntranceActivity.this, currStream.getChannelStream().getName(), Toast.LENGTH_SHORT).show();
                    openStreamPage("buyer", currStream.getChannelStream(), currStream);
                }

            }

        });

    }


    public void openStreamPage(String role, ChannelStream sellerStream, Stream currStream){
        Intent intent = new Intent(this, MainActivity.class);
        //check if sellerStream == channelStream?

        intent.putExtra("channelName", currStream.getChannelStream().getName());
        intent.putExtra("appID", getAppID());
        intent.putExtra("streamObj", currStream);
        intent.putExtra("user", user);
        intent.putExtra("channel", channelStream);
        intent.putExtra("seller-stream", sellerStream);
        intent.putExtra("calling-activity", "entrance");
        intent.putExtra("clientRole", Constants.CLIENT_ROLE_AUDIENCE);
        startActivity(intent);
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
        plantOnClickItems(this, item, user, channelStream);
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

    }

}