package iss.workshop.livestreamapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import iss.workshop.livestreamapp.adapters.OrdersAdapter;
import iss.workshop.livestreamapp.interfaces.IMenuAccess;
import iss.workshop.livestreamapp.interfaces.IStreamDetails;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.Orders;
import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.User;
import iss.workshop.livestreamapp.services.OrdersApi;
import iss.workshop.livestreamapp.services.RetroFitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends AppCompatActivity implements IMenuAccess, IStreamDetails {

    private User user;
    private ChannelStream channel;
    private Stream currStream;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    ListView orders_listview;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        //get channel
        channel = (ChannelStream) intent.getSerializableExtra("channel");
        setupSidebarMenu();

        //set up dialog in activity
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.orderproduct_details_listview);


        //fetch order API
        RetroFitService rfServ = new RetroFitService("orders");
        OrdersApi ordersAPI = rfServ.getRetrofit().create(OrdersApi.class);

        ordersAPI.getChannelOrders(channel.getId()).enqueue(new Callback<List<Orders>>() {
            @Override
            public void onResponse(Call<List<Orders>> call, Response<List<Orders>> response) {
                if(response.code() == 200) {
                    populateOrdersList(response.body());
                    Toast.makeText(OrdersActivity.this, response.body().size() + " orders found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Orders>> call, Throwable t) {
                Toast.makeText(OrdersActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        /*
        Button btnViewPending = findViewById(R.id.btn_view_pending);
        btnViewPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetroFitService rfServ = new RetroFitService("orders");
                OrdersApi ordersAPI = rfServ.getRetrofit().create(OrdersApi.class);

                ordersAPI.getChannelOrdersPendingByUserId(user.getId()).enqueue(new Callback<List<Orders>>() {
                    @Override
                    public void onResponse(Call<List<Orders>> call, Response<List<Orders>> response) {
                        if(response.code() == 200) {
                            populateOrdersList(response.body());
                            Toast.makeText(OrdersActivity.this, response.body().size() + " orders found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Orders>> call, Throwable t) {
                        Toast.makeText(OrdersActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button btnViewAll = findViewById(R.id.btn_view_all);
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetroFitService rfServ = new RetroFitService("orders");
                OrdersApi ordersAPI = rfServ.getRetrofit().create(OrdersApi.class);

                ordersAPI.getChannelOrdersHistory(user.getId()).enqueue(new Callback<List<Orders>>() {
                    @Override
                    public void onResponse(Call<List<Orders>> call, Response<List<Orders>> response) {
                        if(response.code() == 200) {
                            populateOrdersList(response.body());
                            Toast.makeText(OrdersActivity.this, response.body().size() + " orders found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Orders>> call, Throwable t) {
                        Toast.makeText(OrdersActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

         */
    }


    public void populateOrdersList(List<Orders> body) {
        orders_listview = findViewById(R.id.orders_listview);
        OrdersAdapter ordersAdapter = new OrdersAdapter(this,body, user,channel, dialog);
        orders_listview.setAdapter(ordersAdapter);

        orders_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }
}