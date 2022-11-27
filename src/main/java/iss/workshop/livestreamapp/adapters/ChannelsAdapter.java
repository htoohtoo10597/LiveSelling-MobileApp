package iss.workshop.livestreamapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import iss.workshop.livestreamapp.ChannelProfileActivity;
import iss.workshop.livestreamapp.R;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.User;

public class ChannelsAdapter extends BaseAdapter {

    private Context context;
    private List<ChannelStream> channels;
    private User user;
    private ChannelStream channelOfUser;

    public ChannelsAdapter(Context context, List<ChannelStream> channels, User user, ChannelStream channelOfUser){
        this.context = context;
        this.channels = channels;
        this.channelOfUser = channelOfUser;
        this.user = user;
    }

    @Override
    public int getCount() {
        return channels.size();
    }

    @Override
    public Object getItem(int i) {
        return channels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.channel_display_row, viewGroup, false);
        }

        ChannelStream channel = channels.get(i);

        TextView channelName = view.findViewById(R.id.channel_name);
        channelName.setText(channel.getName());

        TextView userName = view.findViewById(R.id.user_name);
        userName.setText(channel.getUser().getFirstName() + " " + channel.getUser().getLastName());

        Button viewChannel = view.findViewById(R.id.btn_view);
        viewChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChannelProfile(channels.get(i));
            }
        });

        return view;
    }

    private void openChannelProfile(ChannelStream channelToView) {
        Intent intent = new Intent(context, ChannelProfileActivity.class);
        intent.setAction("view-as-other");
        intent.putExtra("user", user);
        intent.putExtra("channel", channelOfUser);
        intent.putExtra("channel-to-view", channelToView);
        context.startActivity(intent);
    }


}
