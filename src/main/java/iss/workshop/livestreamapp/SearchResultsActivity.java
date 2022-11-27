package iss.workshop.livestreamapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.agora.rtc2.Constants;
import iss.workshop.livestreamapp.adapters.ChStreamAdapter;
import iss.workshop.livestreamapp.interfaces.IMenuAccess;
import iss.workshop.livestreamapp.interfaces.IStreamDetails;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.User;
import iss.workshop.livestreamapp.services.FetchStreamLog;
import iss.workshop.livestreamapp.services.RetroFitService;
import iss.workshop.livestreamapp.services.StreamsApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsActivity extends AppCompatActivity implements IStreamDetails {

    private User user;
    private ChannelStream channel;
    private String searchTerm;
    private ListView searchResults;
    private TextView searchTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        //get channel
        channel = (ChannelStream) intent.getSerializableExtra("channel");
        searchTerm = intent.getStringExtra("searchTerm");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchTotal = findViewById(R.id.search_total);

        searchResults = findViewById(R.id.search_results);
        RetroFitService rfServ = new RetroFitService("stream");
        StreamsApi streamAPI = rfServ.getRetrofit().create(StreamsApi.class);

        streamAPI.getStreamsBySearchTerm(searchTerm).enqueue(new Callback<List<Stream>>() {
            @Override
            public void onResponse(Call<List<Stream>> call, Response<List<Stream>> response)
            {
                if(response.code() == 200){
                    populateStreamList(response.body());
                } else {
                    Toast.makeText(SearchResultsActivity.this, "no body to present", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Stream>> call, Throwable t) {
                Toast.makeText(SearchResultsActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        //run api to search for streams
    }

    private void populateStreamList(List<Stream> body) {
        ChStreamAdapter streamAdapter = new ChStreamAdapter(this, body, false, user);
        searchResults.setAdapter(streamAdapter);

        searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView channelNameTxt = view
                        .findViewById(R.id.entire_row)
                        .findViewById(R.id.top_container)
                        .findViewById(R.id.text_fields)
                        .findViewById(R.id.channel_name);
                String channelName = channelNameTxt.getText().toString();

                Stream currStream = (Stream) streamAdapter.getItem(i);
                if (currStream.getChannelStream().getUser().getId().equals(user.getId())){
                    Toast.makeText(SearchResultsActivity.this, "This is your stream! Start your stream in your My Streams Page.", Toast.LENGTH_SHORT).show();
                } else {
                    invokeToken(currStream.getChannelStream());
                    Toast.makeText(SearchResultsActivity.this, currStream.getChannelStream().getName(), Toast.LENGTH_SHORT).show();
                    openStreamPage("buyer", currStream.getChannelStream(), currStream);
                }
            }

        });
        searchTotal.setText("Search results for the term \"" + searchTerm + ": " + body.size() + " results." );
    }

    private void openStreamPage(String user, ChannelStream channelStream, Stream currStream) {
        Intent intent = new Intent(this, MainActivity.class);
        //check if sellerStream == channelStream?
        intent.putExtra("channelName", currStream.getChannelStream().getName());
        intent.putExtra("appID", getAppID());
        intent.putExtra("streamObj", currStream);
        intent.putExtra("user", user);
        intent.putExtra("channel", channel);
        intent.putExtra("seller-stream", channelStream);
        intent.putExtra("calling-activity", "entrance");
        intent.putExtra("clientRole", Constants.CLIENT_ROLE_AUDIENCE);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}