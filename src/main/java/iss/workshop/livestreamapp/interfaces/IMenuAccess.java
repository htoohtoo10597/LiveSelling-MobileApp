package iss.workshop.livestreamapp.interfaces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;


import iss.workshop.livestreamapp.ChannelProfileActivity;
import iss.workshop.livestreamapp.MyProductsActivity;


import iss.workshop.livestreamapp.DashboardActivity;

import iss.workshop.livestreamapp.OrdersActivity;
import iss.workshop.livestreamapp.MyPurchasesActivity;

import iss.workshop.livestreamapp.ScheduleStreamActivity;
import iss.workshop.livestreamapp.EntranceActivity;
import iss.workshop.livestreamapp.LoginActivity;
import iss.workshop.livestreamapp.MyStreamsActivity;
import iss.workshop.livestreamapp.R;
import iss.workshop.livestreamapp.TestActivity;
import iss.workshop.livestreamapp.ViewChannelsActivity;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.User;

public interface IMenuAccess extends NavigationView.OnNavigationItemSelectedListener {

    @SuppressLint("NonConstantResourceId")
    default void plantOnClickItems(Context context, MenuItem item, User user, ChannelStream channelStream){

        Class<?> pageToOpen = null;

        switch(item.getItemId()) {
            case R.id.nav_home:
                pageToOpen = EntranceActivity.class;
                break;
            case R.id.nav_channels:
                Toast.makeText(context, "Channels", Toast.LENGTH_SHORT).show();
                pageToOpen = ViewChannelsActivity.class;
                break;
            case R.id.nav_purchases:
                Toast.makeText(context, "Purchases", Toast.LENGTH_SHORT).show();
                pageToOpen = MyPurchasesActivity.class;
                break;
            case R.id.nav_products:
                Toast.makeText(context, "Products", Toast.LENGTH_SHORT).show();
                pageToOpen = MyProductsActivity.class;
                break;
            case R.id.nav_streams:
                Toast.makeText(context, "Streams", Toast.LENGTH_SHORT).show();
                pageToOpen = MyStreamsActivity.class;
                break;
            case R.id.nav_dashboard:
                Toast.makeText(context, "Dashboard", Toast.LENGTH_SHORT).show();
                pageToOpen = DashboardActivity.class;
                break;
            case R.id.nav_myProfile:
                //Toast.makeText(context, "Dashboard", Toast.LENGTH_SHORT).show();
                pageToOpen = ChannelProfileActivity.class;
                break;
            case R.id.nav_orders:
                Toast.makeText(context, "Orders", Toast.LENGTH_SHORT).show();
                pageToOpen = OrdersActivity.class;
                break;
            case R.id.nav_logout:
                pageToOpen = LoginActivity.class;
                break;
        }
        if (!context.getClass().getName().equals(pageToOpen.getName())){
            Intent intent = new Intent(context, pageToOpen);
            intent.putExtra("user", user);
            intent.putExtra("channel", channelStream);
            if (pageToOpen == LoginActivity.class){
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else if (pageToOpen == ChannelProfileActivity.class){
                intent.setAction("view-as-own");
                intent.putExtra("channel-to-view", channelStream);
            }
            context.startActivity(intent);
        }
    }

    void setupSidebarMenu();

}
