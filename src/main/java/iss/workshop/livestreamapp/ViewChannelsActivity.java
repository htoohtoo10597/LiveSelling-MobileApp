package iss.workshop.livestreamapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import iss.workshop.livestreamapp.adapters.ChannelsAdapter;
import iss.workshop.livestreamapp.interfaces.IMenuAccess;
import iss.workshop.livestreamapp.interfaces.IStreamDetails;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.User;
import iss.workshop.livestreamapp.services.ChannelsApi;
import iss.workshop.livestreamapp.services.RetroFitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewChannelsActivity extends AppCompatActivity implements IMenuAccess, IStreamDetails {

    private User user;
    private ChannelStream channel;
    private Stream currStream;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView channelList;
    private TextView listSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_channels);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        //get channel
        channel = (ChannelStream) intent.getSerializableExtra("channel");
        invokeToken(channel);
        setupSidebarMenu();

        channelList = findViewById(R.id.channelsList);
        listSize = findViewById(R.id.channels_count);

        RetroFitService rfServ = new RetroFitService("channel");
        ChannelsApi channelAPI = rfServ.getRetrofit().create(ChannelsApi.class);
        channelAPI.getAllVerifiedChannels(channel.getId()).enqueue(new Callback<List<ChannelStream>>() {
            @Override
            public void onResponse(Call<List<ChannelStream>> call, Response<List<ChannelStream>> response) {
                populateListView(response.body());
            }

            @Override
            public void onFailure(Call<List<ChannelStream>> call, Throwable t) {

            }
        });

    }

    private void populateListView(List<ChannelStream> body) {
        ChannelsAdapter channelsAdapter = new ChannelsAdapter(this, body, user, channel);
        channelList.setAdapter(channelsAdapter);
        listSize.setText(Integer.toString(channelsAdapter.getCount()));
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
        plantOnClickItems(this, item, user, channel);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}