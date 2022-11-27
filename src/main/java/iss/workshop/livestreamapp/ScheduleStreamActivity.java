package iss.workshop.livestreamapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import iss.workshop.livestreamapp.interfaces.IMenuAccess;
import iss.workshop.livestreamapp.interfaces.IStreamDetails;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.User;

public class ScheduleStreamActivity extends AppCompatActivity implements IMenuAccess, IStreamDetails {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    EditText dateTxt, timeTxt, mEName, mEDes;
    ImageView cal,clock;
    private int mDate, mMonth, mYear;
    private int mHour, mMinute;
    private User user;
    private ChannelStream channel;
    private Dialog dialog;
    ListView prodListView;
    Button AddProduct,CreateEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_stream);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        channel = (ChannelStream) intent.getSerializableExtra("channel");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setupSidebarMenu();

        //Calendar
        dateTxt = findViewById(R.id.date);
        cal = findViewById(R.id.datePicker);
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar Cal = Calendar.getInstance();
                mDate = Cal.get(Calendar.DATE);
                mMonth = Cal.get(Calendar.MONTH);
                mYear = Cal.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleStreamActivity.this, android.R.style.Theme_DeviceDefault_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        dateTxt.setText(date +"/"+ (month+1) +"/"+year );
                    }
                }, mYear, mMonth, mDate);
                datePickerDialog.show();
            }
        });
        //chooseDate
        timeTxt = findViewById(R.id.time);
        clock = findViewById(R.id.timePicker);
        clock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Initialize time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleStreamActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                //Initialize hour and minutes
                                mHour = hourOfDay;
                                mMinute = minute;
                                //Initialize calendar
                                Calendar calendar = Calendar.getInstance();
                                //set hour minitues
                                calendar.set(0,0, 0, mHour, mMinute);
                                //set selected time on text view
                                timeTxt.setText(DateFormat.format("hh:mm aa", calendar));
                            }
                        },12, 0, false);
                //Displayed previous selected time
                timePickerDialog.updateTime(mHour, mMinute);
                //show dialog
                timePickerDialog.show();
            }
        });
        mEName = findViewById(R.id.eventName);


        //AddProduct = findViewById(R.id.addProductBtn);
        CreateEvent = findViewById(R.id.createEventBtn);

        CreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent response = new Intent();
                String streamName = mEName.getText().toString();

                String sDate1 = dateTxt.getText().toString();
                String sTime1 = timeTxt.getText().toString();
                String str = sDate1 + " " + sTime1;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy hh:mm a");
                LocalDateTime schedule = LocalDateTime.parse(str, formatter);
                LocalDateTime now = LocalDateTime.now();
                if(schedule.isBefore(LocalDateTime.now())){
                    Toast.makeText(ScheduleStreamActivity.this, "Cannot schedule a stream earlier than now. Check the schedule again.", Toast.LENGTH_SHORT).show();
                } else {
                    response.putExtra("user", user);
                    response.putExtra("channel", channel);
                    response.putExtra("title", streamName);
                    response.putExtra("schedule", schedule);
                    //response.putExtra(“computedSum", 100);
                    setResult(RESULT_OK, response);
                    finish();
                }
            }
        });

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

    //make nav clickable
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    @Override
    public void setupSidebarMenu() {
        return;
    }
}