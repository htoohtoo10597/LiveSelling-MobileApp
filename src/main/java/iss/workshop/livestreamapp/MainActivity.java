package iss.workshop.livestreamapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import iss.workshop.livestreamapp.adapters.ChStreamAdapter;
import iss.workshop.livestreamapp.adapters.ProductsListAdapter;
import iss.workshop.livestreamapp.adapters.ProductsStreamAdapter;
import iss.workshop.livestreamapp.interfaces.IStreamDetails;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.OrderProduct;
import iss.workshop.livestreamapp.models.Orders;
import iss.workshop.livestreamapp.models.Product;
import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.StreamLog;
import iss.workshop.livestreamapp.models.User;
import iss.workshop.livestreamapp.services.FetchStreamLog;
import iss.workshop.livestreamapp.services.OrdersApi;
import iss.workshop.livestreamapp.services.ProductsApi;
import iss.workshop.livestreamapp.services.RetroFitService;
import iss.workshop.livestreamapp.services.StreamLogApi;
import iss.workshop.livestreamapp.services.StreamsApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements IStreamDetails {

    // Fill the App ID of your project generated on Agora Console.
    private String appId;
    // Fill the channel name.
    private String channelName;
    // Fill the temp token generated on Agora Console.
    private String token;
    private Stream currStream;
    private TextView numberOfViewers;
    private TextView streamStatus;
    private ChannelStream channel;
    private ChannelStream sellerChannel;
    private RtcEngine mRtcEngine;
    private String prevActivity;
    //audience or host
    private int clientRole;
    //current user logged in
    private User user;
    //button that shows the products
    private Button showProducts;
    private Button sendLike;
    private Dialog dialog;
    private ListView productsListing;
    private List<Product> channelProducts;
    private Button sendOrder;
    private boolean entered = false;
    private StreamLog streamLog;
    private int numLikes;
    private int maxViewers;

    //for orders
    private ProductsStreamAdapter prodStreamAdapter;

    //for real-time-chat
    // TextView to show message records in the UI
    private TextView messageHistory;
    private TextView messageBox;
    // Message content
    private String message_content;
    private Button sendMessage;

    // RTM client instance
    private RtmClient mRtmClient;
    // RTM channel instance
    private RtmChannel mRtmChannel;


    //permissions
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    private IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote host joining the channel to get the uid of the host.
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onRemoteVideoStats(RemoteVideoStats stats) {
            super.onRemoteVideoStats(stats);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        token = getResources().getString(R.string.stream_token);
        //getting intent details
        Intent streamDetails = getIntent();
        appId = getAppID();
        channelName = streamDetails.getStringExtra("channelName");
        clientRole = streamDetails.getIntExtra("clientRole", 0);
        currStream = (Stream) streamDetails.getSerializableExtra("streamObj");
        prevActivity = streamDetails.getStringExtra("calling-activity");
        user = (User) streamDetails.getSerializableExtra("user");
        channel = (ChannelStream) streamDetails.getSerializableExtra("channel");
        invokeToken(channel);
        streamLog = new StreamLog();
        numLikes = 0;
        numberOfViewers = findViewById(R.id.number_viewers);

        //setting dialog box
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.product_list_layout);

        productsListing = dialog.findViewById(R.id.products_list);
        messageHistory = findViewById(R.id.stream_status);
        messageBox = findViewById(R.id.message_to_send);
        sendMessage = findViewById(R.id.send_message);
        sendLike = findViewById(R.id.send_like);

        showProducts = findViewById(R.id.open_product_list);
        //if clientRole is buyer (audience) or seller (broadcaster)
        if(clientRole == Constants.CLIENT_ROLE_BROADCASTER){
            showProducts.setVisibility(View.INVISIBLE);
            sendLike.setVisibility(View.INVISIBLE);
            sellerChannel = channel;
            RetroFitService rfServ = new RetroFitService("save-stream");
            StreamsApi streamAPI = rfServ.getRetrofit().create(StreamsApi.class);
            streamAPI.setStreamToOngoing(currStream.getId()).enqueue(new Callback<Stream>() {
                @Override
                public void onResponse(Call<Stream> call, Response<Stream> response) {
                    Toast.makeText(MainActivity.this, "Stream is now ongoing!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Stream> call, Throwable t) {

                }
            });
        } else {
            sellerChannel = (ChannelStream) streamDetails.getSerializableExtra("seller-stream");
            invokeToken(sellerChannel);
            RetroFitService rfServ = new RetroFitService("get-products");
            ProductsApi prodAPI = rfServ.getRetrofit().create(ProductsApi.class);
            prodAPI.getAllProductsInStore(sellerChannel.getId()).enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                    channelProducts = response.body();
                    populateListView();
                }

                @Override
                public void onFailure(Call<List<Product>> call, Throwable t) {
                }
            });
        }
        //clicking the button opens the dialog
        showProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProductDialog();
            }
        });
        //get products from channel

        //
        //Toast.makeText(this, currStream.getName(), Toast.LENGTH_SHORT).show();
        TextView txtName = findViewById(R.id.channel_name);
        txtName.setText(channelName);

        //hide top bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
            initializeAndJoinChannel();
        }

        //join rtm
        try {
            mRtmClient = RtmClient.createInstance(getBaseContext(), appId, new RtmClientListener() {
                @Override
                public void onConnectionStateChanged(int state, int reason) {
                    if (!entered){
                        String text = "Welcome to " + channelName + "! Please remember to be polite and patient with the seller and other participants in the chat." + "\n";
                        writeToMessageHistory(text);
                        entered = true;
                    }
                }

                @Override
                public void onMessageReceived(RtmMessage rtmMessage, String s) {
                    /*
                    String text =  user.getUsername() + ": " + rtmMessage.getText() + "\n\n";
                    writeToMessageHistory(text);

                     */
                }

                @Override
                public void onImageMessageReceivedFromPeer(RtmImageMessage rtmImageMessage, String s) {

                }

                @Override
                public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {

                }

                @Override
                public void onMediaUploadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

                }

                @Override
                public void onMediaDownloadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

                }

                @Override
                public void onTokenExpired() {

                }

                @Override
                public void onTokenPrivilegeWillExpire() {

                }

                @Override
                public void onPeersOnlineStatusChanged(Map<String, Integer> map) {

                }
            });
        } catch (Exception e) {
            throw new RuntimeException("RTM initialization failed!");
        }

        String userToken = getUserTokenForRTM(user);

        mRtmClient.login(userToken, user.getUsername(), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                CharSequence text = "User: " + user.getUsername() + " failed to log in to the RTM system!" + errorInfo.toString();
                int duration = Toast.LENGTH_SHORT;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                        toast.show();
                    }
                });

            }
        });

        RtmChannelListener mRtmChannelListener = new RtmChannelListener() {
            @Override
            public void onMemberCountUpdated(int i) {
                if (i == 1){
                    numberOfViewers.setText(i + " Viewer");
                } else {
                    numberOfViewers.setText(i + " Viewers");
                }
                if (maxViewers < i){
                    maxViewers = i;
                }
            }

            @Override
            public void onAttributesUpdated(List<RtmChannelAttribute> list) {

            }

            @Override
            public void onMessageReceived(RtmMessage rtmMessage, RtmChannelMember rtmChannelMember) {
                String message_text = "";
                if(rtmMessage.getText().startsWith("LIKE")){
                    message_text = rtmMessage.getText() + "\n\n";
                    numLikes++;
                } else {
                    message_text = rtmChannelMember.getUserId() + " : " + rtmMessage.getText() + "\n\n";
                }

                writeToMessageHistory(message_text);
            }

            @Override
            public void onImageMessageReceived(RtmImageMessage rtmImageMessage, RtmChannelMember rtmChannelMember) {

            }

            @Override
            public void onFileMessageReceived(RtmFileMessage rtmFileMessage, RtmChannelMember rtmChannelMember) {

            }

            @Override
            public void onMemberJoined(RtmChannelMember rtmChannelMember) {

            }

            @Override
            public void onMemberLeft(RtmChannelMember rtmChannelMember) {

            }
        };

        try {
            // Create an RTM channel
            mRtmChannel = mRtmClient.createChannel(channelName, mRtmChannelListener);
        } catch (RuntimeException e) {
        }
        // Join the RTM channel
        mRtmChannel.join(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                CharSequence text = "User: " + user.getUsername() + " failed to join the channel!" + errorInfo.toString();
                int duration = Toast.LENGTH_SHORT;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                        toast.show();
                    }
                });

            }
        });

    }

    private void writeToMessageHistory(String message_text) {
        messageHistory.append(message_text);
    }


    //go to https://webdemo.agora.io/token-builder/ to generate for the users, then
    // put in the strings.xml (app id, app cert and username):
    private String getUserTokenForRTM(User user) {
        switch(user.getUsername()){
            case "jamesseah":
                return getResources().getString(R.string.token_for_jamesseah);
            case "amandachong":
                return getResources().getString(R.string.token_for_amandachong);
            default:
                String userToken = "token_for_" + user.getUsername();
                String packageName = getPackageName();
                int resId = getResources().getIdentifier(userToken, "string", packageName);
                return getResources().getString(resId);
        }

    }

    public void onClickSendChannelMsg(View v)
    {
        message_content = messageBox.getText().toString();

        // Create RTM message instance
        RtmMessage message = mRtmClient.createMessage();
        message.setText(message_content);

        // Send message to channel
        mRtmChannel.sendMessage(message, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SpannableString usernameTxt = new SpannableString(user.getUsername());
                usernameTxt.setSpan(new StyleSpan(Typeface.BOLD), 0, user.getUsername().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                String text = usernameTxt  + ": " + message.getText() + "\n\n";
                writeToMessageHistory(text);
                messageBox.setText("");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                String text = "Message fails to send to channel " + mRtmChannel.getId() + " Error: " + errorInfo + "\n";
                writeToMessageHistory(text);
            }
        });
    }

    public void onClickSendLikeToChannel(View v)
    {
        RtmMessage message = mRtmClient.createMessage();
        message.setText("LIKE: You sent a like to " + channelName + "!");

        // Send message to channel
        mRtmChannel.sendMessage(message, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                messageBox.setText("");
                writeToMessageHistory(message.getText() + "\n\n");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                String text = "Message fails to send to channel " + mRtmChannel.getId() + " Error: " + errorInfo + "\n";
                writeToMessageHistory(text);
            }
        });
    }

    private void populateListView() {
            prodStreamAdapter = new ProductsStreamAdapter(this, channelProducts);
            productsListing.setAdapter(prodStreamAdapter);
            sendOrder = dialog.findViewById(R.id.send_order);
            sendOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createOrder();
                }
            });

    }

    //function to do the order
    private void createOrder() {
        List<Product> productsToOrder = prodStreamAdapter.getProducts();
        List<Integer> amtToOrder = prodStreamAdapter.getProductQty();
        Orders newOrder = new Orders(user, null);
        //newOrder.setChannel(sellerChannel);

        for(int i = 0; i < productsToOrder.size(); i++){
            if(amtToOrder.get(i) > 0){
                OrderProduct orderProd = new OrderProduct();
                //orderProd.setOrder(newOrder);
                orderProd.setChannel(sellerChannel);
                orderProd.setProduct(productsToOrder.get(i));
                orderProd.setQuantity(amtToOrder.get(i));

                newOrder.getOrderProduct().add(orderProd);
            }
        }

        RetroFitService rfServ = new RetroFitService("new-orders");
        OrdersApi orderAPI = rfServ.getRetrofit().create(OrdersApi.class);
        orderAPI.addNewOrder(newOrder, user.getId(), sellerChannel.getId(), currStream.getId()).enqueue(new Callback<Orders>() {
            @Override
            public void onResponse(Call<Orders> call, Response<Orders> response) {
                if (response.code() == 201){
                    Toast.makeText(MainActivity.this, "Success! Your order has been sent.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    for(int i = 0; i < amtToOrder.size(); i++){
                        amtToOrder.set(i, 0);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Got the response, wrong body", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Orders> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Order failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }

    private void initializeAndJoinChannel() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }
        // By default, video is disabled, and you need to call enableVideo to start a video stream.
        mRtcEngine.enableVideo();

        // Start local preview.
        mRtcEngine.startPreview();

        //mRtcEventHandler.onUserJoined();

        FrameLayout container = findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = new SurfaceView (getBaseContext());
        container.addView(surfaceView);

        ChannelMediaOptions options = new ChannelMediaOptions();
        // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
        options.clientRoleType = clientRole;
        // For the Interactive Live Streaming Standard scenario, the user level of an audience member needs to be AUDIENCE_LATENCY_LEVEL_LOW_LATENCY.
        options.audienceLatencyLevel = Constants.AUDIENCE_LATENCY_LEVEL_LOW_LATENCY;
        // For a live streaming scenario, set the channel profile as BROADCASTING.
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;

        if (options.clientRoleType == Constants.CLIENT_ROLE_BROADCASTER){
            mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));
            mRtcEngine.enableAudio();
            //start fetching of stream log
            Intent intent = new Intent(MainActivity.this, FetchStreamLog.class);
            intent.setAction("send_messages");
            intent.putExtra("seller", user);
            intent.putExtra("stream", currStream);
            intent.putExtra("duration", 5);
            //startService(intent);
        }
        // Pass the SurfaceView object to Agora so that it renders the local video.

        // Join the channel with a temp token.
        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
        Random rd = new Random();
        mRtcEngine.joinChannel(token, channelName, rd.nextInt(100), options);
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = new SurfaceView (getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
    }

    private void openProductDialog(){
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (getResources().getDisplayMetrics().heightPixels*0.60));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MainActivity.this, FetchStreamLog.class);
                stopService(intent);
                //stop stream
                mRtcEngine.stopPreview();
                mRtcEngine.leaveChannel();
                //stop chat
                mRtmClient.logout(null);
                mRtmChannel.leave(null);
                if(clientRole == Constants.CLIENT_ROLE_BROADCASTER){
                    streamLog = new StreamLog();
                    streamLog.setNumLikes(numLikes);
                    streamLog.setNumViewers(maxViewers);
                    //write new API
                    RetroFitService rfServ = new RetroFitService("save-logs");
                    StreamLogApi streamLogAPI = rfServ.getRetrofit().create(StreamLogApi.class);
                    streamLogAPI.addNewLogList(streamLog, user.getId(), currStream.getId()).enqueue(new Callback<StreamLog>() {
                        @Override
                        public void onResponse(Call<StreamLog> call, Response<StreamLog> response) {
                            Toast.makeText(MainActivity.this, "Stream Log have been saved!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<StreamLog> call, Throwable t) {

                        }
                    });

                }
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}