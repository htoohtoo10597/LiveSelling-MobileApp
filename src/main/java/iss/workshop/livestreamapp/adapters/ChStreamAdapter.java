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
import android.widget.Toast;

import com.google.android.material.chip.Chip;

import io.agora.rtc2.Constants;
import iss.workshop.livestreamapp.MainActivity;
import iss.workshop.livestreamapp.MyStreamsActivity;
import iss.workshop.livestreamapp.R;

import java.time.format.DateTimeFormatter;
import java.util.List;

import iss.workshop.livestreamapp.helpers.StreamStatus;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.User;
import iss.workshop.livestreamapp.services.RetroFitService;
import iss.workshop.livestreamapp.services.StreamsApi;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChStreamAdapter extends BaseAdapter {

    private Context context;
    protected List<Stream> streams;
    protected boolean myStreamOrNot;
    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a");
    private User user;
    private ChannelStream channel;



    public ChStreamAdapter (Context context, List<Stream> streams, boolean mystreamornot, User user){
        this.context = context;
        this.streams = streams;
        this.myStreamOrNot = mystreamornot;
        this.user = user;
    }

    public ChStreamAdapter (Context context, List<Stream> streams, boolean mystreamornot, User user, ChannelStream channel){
        this.context = context;
        this.streams = streams;
        this.myStreamOrNot = mystreamornot;
        this.user = user;
        this.channel = channel;
    }

    @Override
    public int getCount() {
        return streams.size();
    }

    @Override
    public Object getItem(int i) {
        return streams.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (myStreamOrNot){
                view = inflater.inflate(R.layout.my_stream_row, viewGroup, false);
            } else {
                view = inflater.inflate(R.layout.channelrow, viewGroup, false);
            }
        }

        Stream currentStream = streams.get(i);

        //channel name
        TextView channelName = (TextView) view
                .findViewById(R.id.entire_row)
                .findViewById(R.id.top_container)
                .findViewById(R.id.text_fields)
                .findViewById(R.id.channel_name);
        channelName.setText(currentStream.getChannelStream().getName());

        //stream name
        TextView streamName = (TextView) view
                .findViewById(R.id.entire_row)
                .findViewById(R.id.top_container)
                .findViewById(R.id.text_fields)
                .findViewById(R.id.stream_name);
        streamName.setText(currentStream.getTitle());

        TextView streamDesc = (TextView) view
                .findViewById(R.id.entire_row)
                .findViewById(R.id.top_container)
                .findViewById(R.id.text_fields)
                .findViewById(R.id.stream_desc);
        streamDesc.setText(currentStream.getChannelStream().getUser().getFirstName() + " " + currentStream.getChannelStream().getUser().getLastName());

        //stream name
        TextView streamDate = (TextView) view
                .findViewById(R.id.bottom_container)
                .findViewById(R.id.date_of_stream);
        streamDate.setText(currentStream.getSchedule().format(df));

        if (myStreamOrNot){
            Button btnCheckStreams = view
                    .findViewById(R.id.bottom_container)
                    .findViewById(R.id.btn_check_stream);
            btnCheckStreams.setText("Start");

            btnCheckStreams.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Stream stream = streams.get(i);
                    Toast.makeText(context, stream.getTitle(), Toast.LENGTH_SHORT).show();
                    openStreamPage("seller", stream);
                }
            });

            Button btnDeleteStream = view
                    .findViewById(R.id.bottom_container)
                    .findViewById(R.id.btn_delete_stream);

            btnDeleteStream.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Stream stream = streams.get(i);
                    RetroFitService rfServ = new RetroFitService("delete-stream");
                    StreamsApi streamAPI = rfServ.getRetrofit().create(StreamsApi.class);
                    streamAPI.deleteStream(stream.getId()).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.code() == 200){
                                streams.remove(stream);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Stream has been deleted!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
            });
        } else {
            Chip liveChip = (Chip) view
                    .findViewById(R.id.bottom_container)
                    .findViewById(R.id.live_chip);

            if (currentStream.getStatus() == StreamStatus.PENDING) {
                liveChip.setText("SCHEDULED");
                liveChip.setChipBackgroundColor(context.getResources().getColorStateList(R.color.grey, null));
            } else if (currentStream.getStatus() == StreamStatus.ONGOING) {
                liveChip.setText("ONGOING");
                liveChip.setChipBackgroundColor(context.getResources().getColorStateList(R.color.red,null));
            }

        }


        return view;
    }

    private void openStreamPage(String role, Stream currStream) {
        Intent intent = new Intent(context, MainActivity.class);
        //check if sellerStream == channelStream?

        intent.putExtra("channelName", channel.getName());
        intent.putExtra("appID", context.getResources().getString(R.string.appId));
        intent.putExtra("streamObj", currStream);
        intent.putExtra("user", user);
        intent.putExtra("channel", channel);
        intent.putExtra("seller-stream", channel);
        intent.putExtra("calling-activity", "mystreams");
        intent.putExtra("clientRole", Constants.CLIENT_ROLE_BROADCASTER);

        context.startActivity(intent);
    }


}
