package iss.workshop.livestreamapp;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.annotation.NonNull;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import iss.workshop.livestreamapp.interfaces.IMenuAccess;
import iss.workshop.livestreamapp.interfaces.IStreamDetails;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.User;
import iss.workshop.livestreamapp.services.DashboardApi;
import iss.workshop.livestreamapp.services.RetroFitService;
import iss.workshop.livestreamapp.services.StreamsApi;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements IStreamDetails, IMenuAccess {

    Context context;
    ChannelStream channelStream;
    private Spinner spinnerCategory, spinnerDay, spinnerPeriod;
    private Button btnPredict;
    private User user;
    private RetroFitService rfServ;
    private RetroFitService pdrfServ;
    private DashboardApi dashboardApi;
    private DashboardApi dashboardPredictionApi;
    private StreamsApi streamAPI;
    String[] userlikes = {""};
    String[] othersLikes = {""};
    List<Stream> streams;

    List<String> categoryList = new ArrayList<>(Arrays.asList
            ("Clothing", "Food", "Home Appliances", "Furnitures", "Electronics Devices",
                    "Baby Items and Toys", "Health and Beauty", "Sports Items", "Groceries", "Others"));
    List<String> dayList = new ArrayList<>(Arrays.asList
            ("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));
    List<String> timePeriodList = new ArrayList<>(Arrays.asList
            ("12am-6am", "6am-12pm", "12pm-6pm", "6pm-12am"));


    private ChannelStream channel;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        rfServ = new RetroFitService("stream");
        pdrfServ = new RetroFitService("prediction");
        dashboardApi = rfServ.getRetrofit().create(DashboardApi.class);
        dashboardPredictionApi = pdrfServ.getRetrofit().create(DashboardApi.class);
        final Handler handler = new Handler();
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        channel = (ChannelStream) intent.getSerializableExtra("channel");
        //invokeToken(channel);
        setupSidebarMenu();

        addItemsOnSpinnerCategory();
        addItemsOnSpinnerDay();
        addItemsOnSpinnerPeriod();
        addListenerOnPredictionButton();

        //update features
        setUserRating();
        setPopularityView();
        setPengingOrders();
        updatePrediction();

        //View All Streams
        Button btnViewAllStream = findViewById(R.id.view_all_stream);
        btnViewAllStream.setOnClickListener(view -> {
            Intent intent2 = new Intent(this, MyStreamsActivity.class);
            intent2.putExtra("user", user);
            intent2.putExtra("channel", channel);
            startActivity(intent2);
        });
        //View Pending orders
        TextView pendingOrders = findViewById(R.id.pending_order_count);
        pendingOrders.setOnClickListener(view -> {

            Intent intent2 = new Intent(this, OrdersActivity.class);
            intent2.putExtra("user", user);
            intent2.putExtra("channel", channel);
            startActivity(intent2);
        });
        // update all the charts
        setPopularityChart();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setTimeSeriesChart();
            }
        }, 1000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getOrderByTimePeriodChart();
            }
        }, 2000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getUpcomingStreams();
            }
        }, 3000);
    }
    public void addItemsOnSpinnerCategory() {
        spinnerCategory = (Spinner) findViewById(R.id.spinner_category);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categoryList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(dataAdapter);
    }
    public void addItemsOnSpinnerDay() {
        spinnerDay = (Spinner) findViewById(R.id.spinner_day);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dayList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dataAdapter);
    }
    public void addItemsOnSpinnerPeriod() {
        spinnerPeriod = (Spinner) findViewById(R.id.spinner_period);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, timePeriodList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriod.setAdapter(dataAdapter);
    }
    public void addListenerOnPredictionButton() {
        btnPredict = findViewById(R.id.btn_predict);
        btnPredict.setOnClickListener(v -> updatePrediction());
    }
    public void updatePrediction(){
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerDay = findViewById(R.id.spinner_day);
        spinnerPeriod = findViewById(R.id.spinner_period);
        btnPredict = findViewById(R.id.btn_predict);
        String[] categoryMapping = {"CLOTHING", "FOOD", "APPLIANCES", "FURNITURES",
                "TECHNOLOGY", "BABY", "HEALTH", "SPORTS", "GROCERIES", "OTHERS"};
        String[] dayMapping =
                {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        TextView expectedOrders = findViewById(R.id.expected_orders);
        TextView expectedViewers = findViewById(R.id.expected_viewers);
        String selectedCategory = String.valueOf(spinnerCategory.getSelectedItem());
        String selectedDay = String.valueOf(spinnerDay.getSelectedItem());
        String mappedCategory = categoryMapping[categoryList.indexOf(selectedCategory)];
        String mappedDay = dayMapping[dayList.indexOf(selectedDay)];
        String selectedPeriod = String.valueOf(spinnerPeriod.getSelectedItem());
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("product_category", mappedCategory)
                .addFormDataPart("day", mappedDay)
                .addFormDataPart("time_period", selectedPeriod)
                .build();
        dashboardPredictionApi.predictOrdersAndViewers(requestBody).enqueue(new Callback<List<Map<String, String>>>() {
            @Override
            public void onResponse(Call<List<Map<String, String>>> call, Response<List<Map<String, String>>> response) {
                List<Map<String, String>> predictionResult  = response.body();
                expectedOrders.setText(predictionResult.get(0).get("order"));
                expectedViewers.setText(predictionResult.get(1).get("viewer"));

            }
            @Override
            public void onFailure(Call<List<Map<String, String>>> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setUserRating() {
        dashboardApi.getUserAverageRating(user.getId()).enqueue(new Callback<String>() {
            TextView ratingView = findViewById(R.id.rating_view);
            RatingBar ratingBar = findViewById(R.id.rating_bar_dash);
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                ratingView.setText(response.body()+"/5");
                ratingBar.setRating(Float.parseFloat(response.body()));
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setPopularityView() {

        TextView popularityView = findViewById(R.id.popularity_view);
        dashboardApi.getUserAverageLikes(user.getId()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                userlikes[0] = (response.body());
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        dashboardApi.getAverageStreamLikes().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                othersLikes[0] = (response.body());
             }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setPopularity(userlikes, othersLikes);
            }
        }, 1000);
    }
    public void setPopularity(String[] userslikes, String[]  othersLikes) {
        Integer userlikeCount = Integer.parseInt(userslikes[0]);
        Integer otherlikeCount = Integer.parseInt(othersLikes[0]);
        TextView popularityView = findViewById(R.id.popularity_view);
        if (userlikeCount == 0 ) {
            popularityView.setText("You haven't received any reactions yet. "+
                    "Good luck on your next stream!");
        }
        else if (userlikeCount < otherlikeCount && userlikeCount != 0) {
            popularityView.setText("You are getting less Hearts than other streamers. "+
                    "Try to improve. Good luck!");
        }
        else if (userlikeCount < otherlikeCount && userlikeCount != 0) {
            popularityView.setText("You are getting less Hearts than other streamers. "+
                    "Try to improve. Good luck!");
        }
        else if (userlikeCount == otherlikeCount && userlikeCount != 0) {
            popularityView.setText("You are getting same number of Hearts as other streamers. "+
                    "Good job!. Check if you can improve further");
        }
        else if (userlikeCount > otherlikeCount) {
            popularityView.setText("You are getting more Hearts than Other Streamers "+
                    "Good job!. Check if you can improve even further");
        }
    }
    public void setTimeSeriesChart(){
        String userId = user.getId();
        String imageUri = "http://10.0.2.2:5000/charts?name=movavg";
        ImageView timeSeries = (ImageView) findViewById(R.id.time_series_chart);

        Picasso.with(this)
                .load(imageUri)
                .resize(600, 200)
                .centerInside()
                .into(timeSeries);
    }
    public void setPopularityChart(){
        String userId = user.getId();
        String imageUri = "http://10.0.2.2:5000/charts?name=popchart&userid="+userId;
        ImageView popularityChart = (ImageView) findViewById(R.id.pop_chart);
        Picasso.with(this)
                .load(imageUri)
                .resize(600, 200)
                .centerInside()
                .into(popularityChart);
    }
    public void getOrderByTimePeriodChart(){
        String imageUri = "http://10.0.2.2:5000/charts?name=bytime";
        ImageView timePeriodChart = (ImageView) findViewById(R.id.time_period_chart);
        Picasso.with(this)
                .load(imageUri)
                .resize(600, 200)
                .centerInside()
                .into(timePeriodChart);
    }
    public void setPengingOrders(){
        TextView pendingOrderView = findViewById(R.id.pending_order_count);
        dashboardApi.getPengingOrders(user.getId()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                pendingOrderView.setText(response.body());
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getUpcomingStreams() {

        dashboardApi.getThreeUserStreamsPending(user.getId()).enqueue(new Callback<List<Stream>>() {
            @Override
            public void onResponse(Call<List<Stream>> call, Response<List<Stream>> response) {
                streams = (response.body());
//                System.out.println(streams);
                setUpcomingScheduleTable();
                setMorePendingCount(streams.size());
            }
            @Override
            public void onFailure(Call<List<Stream>> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        }
        public void setUpcomingScheduleTable() {
            String datePattern = "dd-MM-yyyy";
            String TimePattern = "HH:mm";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TimePattern);
            TableLayout tableLayout = (TableLayout) findViewById(R.id.table_upcoming_stream);
            TableRow tbrow0 = new TableRow(this);

            TextView tv1 = new TextView(this);
            tv1.setText(" Date ");
            tv1.setTextColor(Color.WHITE);
            tv1.setBackgroundColor(Color.BLACK);
            tv1.setPadding(15,5,15,5);
            tbrow0.addView(tv1);
            TextView tv2 = new TextView(this);
            tv2.setText(" Time ");
            tv2.setTextColor(Color.WHITE);
            tv2.setBackgroundColor(Color.BLACK);
            tv2.setPadding(15,5,15, 5);
            tbrow0.addView(tv2);
            TextView tv3 = new TextView(this);
            tv3.setText(" Title ");
            tv3.setTextColor(Color.WHITE);
            tv3.setBackgroundColor(Color.BLACK);
            tv3.setPadding(15,5,15,5);
            tbrow0.addView(tv3);
            tableLayout.addView(tbrow0);
            //dynamically add rows to the table
            for (int i = 0; i < streams.size(); i++) {
                System.out.println(streams.size());
                TableRow tbrow = new TableRow(this);

                TextView t1v = new TextView(this);
                Stream stream = streams.get(i);
                String date = dateFormatter.format(stream.getSchedule());
                String time = timeFormatter.format(stream.getSchedule());
                String title = stream.getTitle();
                t1v.setText(String.valueOf(date));
                t1v.setTextColor(Color.BLACK);
                t1v.setPadding(15,5,15,5);
                tbrow.addView(t1v);
                TextView t2v = new TextView(this);
                t2v.setText(time);
                t2v.setTextColor(Color.BLACK);
                t2v.setPadding(15,5,15,5);
                tbrow.addView(t2v);
                TextView t3v = new TextView(this);
                t3v.setText(title);
                t3v.setSingleLine(true);
                t3v.setEllipsize(TextUtils.TruncateAt.END);
                t3v.setTextColor(Color.BLACK);
                t3v.setPadding(15,5,15,5);
                tbrow.addView(t3v);
                tableLayout.addView(tbrow);
            }
        }
        public void setMorePendingCount(Integer tableCount) {
              TextView moreCount = findViewById(R.id.more_stream);
              dashboardApi.getAllPendingStreamCount(user.getId()).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Integer totalPending = Integer.parseInt(response.body());
                    if (totalPending<=tableCount){
                        moreCount.setText("0" + " More ");
                    }
                    Integer pendingCount = totalPending-tableCount;
                    moreCount.setText(pendingCount+ " More ");
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(DashboardActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
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