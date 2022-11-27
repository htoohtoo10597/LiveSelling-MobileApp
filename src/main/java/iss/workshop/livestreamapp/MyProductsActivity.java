package iss.workshop.livestreamapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import iss.workshop.livestreamapp.adapters.MyProductsAdapter;
import iss.workshop.livestreamapp.adapters.ProductsListAdapter;
import iss.workshop.livestreamapp.helpers.ProductCategories;
import iss.workshop.livestreamapp.interfaces.IMenuAccess;
import iss.workshop.livestreamapp.interfaces.IStreamDetails;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.Orders;
import iss.workshop.livestreamapp.models.Product;
import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.User;
import iss.workshop.livestreamapp.services.ProductsApi;
import iss.workshop.livestreamapp.services.RetroFitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProductsActivity extends AppCompatActivity implements IMenuAccess, IStreamDetails {

    private User user;
    private ChannelStream channel;
    private Stream currStream;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView productsListView;
    private ActivityResultLauncher<Intent> rlAddProduct;
    private ActivityResultLauncher<Intent> rlUpdateProduct;
    private Button btnAdd_product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_products);

        setupSidebarMenu();

        //get user
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        //get channel
        channel = (ChannelStream) intent.getSerializableExtra("channel");
        productsListView = findViewById(R.id.list_products);
        //process of adding
        rlAddProduct = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                Intent productAddedIntent = result.getData();

                String product_name = productAddedIntent.getStringExtra("product_name");
                Double product_price = productAddedIntent.getDoubleExtra("product_price",0);
                String product_desc = productAddedIntent.getStringExtra("product_desc");
                ProductCategories category = (ProductCategories) productAddedIntent.getSerializableExtra("category");
                int quantity = productAddedIntent.getIntExtra("quantity", 1);

                Product product = new Product(product_name, category, product_desc, product_price, quantity, channel);

                RetroFitService rfServ = new RetroFitService("save-product");
                ProductsApi productAPI = rfServ.getRetrofit().create(ProductsApi.class);

                productAPI.addToStore(user.getId(), product).enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        Toast.makeText(MyProductsActivity.this, response.body().getName() + " has been added to your store!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Toast.makeText(MyProductsActivity.this, "Your product was not added to the store. Please try again in a bit.", Toast.LENGTH_SHORT).show();
                    }
                });

                //do the thing you want to happen, add to list
            }
        });

        rlUpdateProduct = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                Intent productToEditIntent = result.getData();
                Product productToEdit = (Product) productToEditIntent.getSerializableExtra("product");
                String product_name = productToEditIntent.getStringExtra("product_name");
                Double product_price = productToEditIntent.getDoubleExtra("product_price",0);
                String product_desc = productToEditIntent.getStringExtra("product_desc");
                ProductCategories category = (ProductCategories) productToEditIntent.getSerializableExtra("category");
                int quantity = productToEditIntent.getIntExtra("quantity", 1);

                Product product = new Product(product_name, category, product_desc, product_price, quantity, channel);

                RetroFitService rfServ = new RetroFitService("save-product");
                ProductsApi productAPI = rfServ.getRetrofit().create(ProductsApi.class);

                productAPI.editProduct(productToEdit.getId(), product).enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        Toast.makeText(MyProductsActivity.this, response.body().getName() + " has been edited!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Toast.makeText(MyProductsActivity.this, "Your product was not added to the store. Please try again in a bit.", Toast.LENGTH_SHORT).show();
                    }
                });

                //do the thing you want to happen, add to list
            } else {
                startActivity(getIntent());
                finish();
                overridePendingTransition(0, 0);
            }
        });

        RetroFitService rfServ = new RetroFitService("get-products");
        ProductsApi prodAPI = rfServ.getRetrofit().create(ProductsApi.class);
        prodAPI.getAllProductsInStore(channel.getId()).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                populateListView(response.body());
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });

        btnAdd_product = findViewById(R.id.add_product);
        btnAdd_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProductsActivity.this, AddProductActivity.class);
                intent.setAction("add-product");
                intent.putExtra("user", user);
                intent.putExtra("channel", channel);
                rlAddProduct.launch(intent);
            }
        });
        //process of adding
    }

    private void populateListView(List<Product> body) {
        TextView numOfProd = findViewById(R.id.num_of_products);
        MyProductsAdapter prodAdapter = new MyProductsAdapter(this, body, rlUpdateProduct);
        productsListView.setAdapter(prodAdapter);
        int num = prodAdapter.getCount();
        numOfProd.setText(Integer.toString(num));
    }


    @Override
    public void setupSidebarMenu() {
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        plantOnClickItems(this, item, user, channel);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }


}