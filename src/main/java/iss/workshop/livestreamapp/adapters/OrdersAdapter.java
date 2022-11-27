package iss.workshop.livestreamapp.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import iss.workshop.livestreamapp.MyPurchasesActivity;
import iss.workshop.livestreamapp.OrdersActivity;
import iss.workshop.livestreamapp.R;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.Message;
import iss.workshop.livestreamapp.models.OrderProduct;
import iss.workshop.livestreamapp.models.Orders;
import iss.workshop.livestreamapp.models.Product;
import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.User;
import iss.workshop.livestreamapp.services.OrdersApi;
import iss.workshop.livestreamapp.services.ProductsApi;
import iss.workshop.livestreamapp.services.RetroFitService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersAdapter extends BaseAdapter {

    private Context context;
    private List<Orders> orders;
    ListView orders_listview;
    private User user;
    private ChannelStream channel;
    private Dialog dialog;
    private ListView orderProductListView;
    //private ActivityResultLauncher<Intent> rlRefresh;
    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a");


    public OrdersAdapter(Context context, List<Orders> orders, User user, ChannelStream channel, Dialog dialog ){
        this.context = context;
        this.orders = orders;
        this.user = user;
        this.channel = channel;
        this.dialog = dialog;
       // this.rlRefresh = rlRefresh;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int i) {
        return orders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.orders_row, viewGroup, false);
        }

        Orders currOrder = orders.get(i);

        TextView txtId = view.findViewById(R.id.order_placed_user);
        txtId.setText(currOrder.getUser().getFirstName() + " " + orders.get(i).getUser().getLastName());

        TextView txtTime = view.findViewById(R.id.order_time);
        txtTime.setText(currOrder.getOrderDateTime().format(df));

        Chip orderStatus = view.findViewById(R.id.btn_order_status);
        orderStatus.setText(currOrder.getOrderStatus().toString());

        //confirm Button for seller
        Button btnConfirm = view.findViewById(R.id.btn_order_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetroFitService rfServ = new RetroFitService("order-status");
                OrdersApi ordersApi = rfServ.getRetrofit().create(OrdersApi.class);
                ordersApi.updateOrderStatus(currOrder.getId(), "CONFIRMED").enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            orders.remove(currOrder);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Order Confirmed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(context,t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        //reject button for seller
        Button btnReject = view.findViewById(R.id.btn_order_reject);
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetroFitService rfServ = new RetroFitService("order-status");
                OrdersApi ordersApi = rfServ.getRetrofit().create(OrdersApi.class);
                ordersApi.updateOrderStatus(currOrder.getId(), "CANCELLED").enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){

                            orders.remove(currOrder);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Order Rejected", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(context,t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //view details button for seller
        Button mOrderDetails = view.findViewById(R.id.view_details);
        mOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetroFitService rfServ = new RetroFitService("order-product");
                ProductsApi productAPI = rfServ.getRetrofit().create(ProductsApi.class);
                productAPI.getProductsInOrder(currOrder.getId()).enqueue(new Callback<List<OrderProduct>>() {
                    @Override
                    public void onResponse(Call<List<OrderProduct>> call, Response<List<OrderProduct>> response) {
                        if (response.code() == 200) {
                            populateDialogList(response.body());
                            Toast.makeText(context, response.body().size() +
                                    " Orders_Product found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<OrderProduct>> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(context,t.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                openProductDialog();
            }
        });
        return view;
    }
    private void openProductDialog(){
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (context.getResources().getDisplayMetrics().heightPixels*0.60));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    private void populateDialogList(List<OrderProduct> body){
        orderProductListView = dialog.findViewById(R.id.order_product_list);
        OrderProductAdapter orderProductAdapter = new OrderProductAdapter(context,body);
        orderProductListView.setAdapter(orderProductAdapter);
    }

}