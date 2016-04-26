package com.imarneanu.fivesteps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText mEventDate;
    private Spinner mEventTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setupView();
    }

    private void initView() {
        mEventDate = (EditText) findViewById(R.id.event_date);
        mEventTime = (Spinner) findViewById(R.id.event_time);
    }

    private void setupView() {
        SimpleDateFormat sdf = new SimpleDateFormat("E, MMM dd, yyyy", Locale.ENGLISH);
        String currentDate = sdf.format(new Date());
        mEventDate.setHint(currentDate);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEventTime.setAdapter(adapter);
    }
}
