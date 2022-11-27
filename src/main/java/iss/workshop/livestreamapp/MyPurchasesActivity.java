package iss.workshop.livestreamapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import iss.workshop.livestreamapp.adapters.PurchaseAdapter;
import iss.workshop.livestreamapp.interfaces.IMenuAccess;
import iss.workshop.livestreamapp.interfaces.IStreamDetails;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.OrderProduct;
import iss.workshop.livestreamapp.models.Orders;
import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.User;
import iss.workshop.livestreamapp.services.OrdersApi;
import iss.workshop.livestreamapp.services.ProductsApi;
import iss.workshop.livestreamapp.services.RetroFitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyPurchasesActivity extends AppCompatActivity implements IMenuAccess, IStreamDetails {

    private User user;
    private ChannelStream channel;
    private Stream currStream;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Button mOrderDetails;
    private Dialog dialog;
    private ActivityResultLauncher<Intent> rlRefresh;
    private ListView purchase_listview;

    public MyPurchasesActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_purchases);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        //get channel
        channel = (ChannelStream) intent.getSerializableExtra("channel");
        invokeToken(channel);
        setupSidebarMenu();

        //setting up dialog
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.orderproduct_details_listview);


        //fetch orders API
        RetroFitService rfServ = new RetroFitService("orders");
        OrdersApi purchasesAPI = rfServ.getRetrofit().create(OrdersApi.class);
        //sending API to our database
        purchasesAPI.getUserPurchases(user.getId()).enqueue(new Callback<List<Orders>>() {
            @Override
            public void onResponse(Call<List<Orders>> call, Response<List<Orders>> response) {
                if(response.code() == 200){
                    populatePurchaseList(response.body());
                    Toast.makeText(MyPurchasesActivity.this, response.body().size() +
                              " Orders found.", Toast.LENGTH_SHORT).show();
                    TextView totalPurchase = findViewById(R.id.total_purchase);
                    totalPurchase.setText(Integer.toString(response.body().size()));
                }
            }
            @Override
            public void onFailure(Call<List<Orders>> call, Throwable t) {
                Toast.makeText(MyPurchasesActivity.this,t.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populatePurchaseList(List<Orders> body) {
        purchase_listview = findViewById(R.id.purchaseList);
        PurchaseAdapter pAdapter = new PurchaseAdapter(this,  body,dialog, user, channel);
        purchase_listview.setAdapter(pAdapter);
    }
    private void openProductDialog(){
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (getResources().getDisplayMetrics().heightPixels*0.60));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
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