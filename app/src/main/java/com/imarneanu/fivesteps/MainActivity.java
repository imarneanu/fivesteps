package com.imarneanu.fivesteps;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class MainActivity extends AppCompatActivity {

    private final int SELECT_PHOTO = 1;
    private final int SELECT_LOCATION = 2;

    private EditText mEventDate;
    private Spinner mEventTime;
    private TextView mEventLocation;
    private Button mPickLocation;
    private RelativeLayout mEventPoster;
    private Button mChangePoster;

    private int mScreenWidth;

    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        initView();
        setupView();
    }

    private void initView() {
        mEventDate = (EditText) findViewById(R.id.event_date);
        mEventTime = (Spinner) findViewById(R.id.event_time);
        mEventLocation = (TextView) findViewById(R.id.event_location);
        mPickLocation = (Button) findViewById(R.id.pick_location);
        mEventPoster = (RelativeLayout) findViewById(R.id.event_poster);
        mChangePoster = (Button) findViewById(R.id.change_poster);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
    }

    private void setupView() {
        SimpleDateFormat sdf = new SimpleDateFormat("E, MMM dd, yyyy", Locale.ENGLISH);
        String currentDate = sdf.format(new Date());
        mEventDate.setHint(currentDate);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEventTime.setAdapter(adapter);

        mPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(mContext), SELECT_LOCATION);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        mChangePoster.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    try {
                        mEventPoster.setBackground(decodeUri(selectedImage));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case SELECT_LOCATION:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(data, this);
                    String location = place.getName() + "\n" + place.getAddress();
                    mEventLocation.setText(location);
                    mEventLocation.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private BitmapDrawable decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, options);

        int width_tmp = options.outWidth, height_tmp = options.outHeight;
        int inSampleSize = 1;

        if (height_tmp > mScreenWidth || width_tmp > mScreenWidth) {

            final int halfHeight = height_tmp / 2;
            final int halfWidth = width_tmp / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > mScreenWidth
                    && (halfWidth / inSampleSize) > mScreenWidth) {
                inSampleSize *= 2;
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        return new BitmapDrawable(getResources(), BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, options));
    }
}
