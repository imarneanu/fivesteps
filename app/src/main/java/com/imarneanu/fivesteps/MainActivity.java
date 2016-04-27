package com.imarneanu.fivesteps;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final int SELECT_PHOTO = 1;

    private EditText mEventDate;
    private Spinner mEventTime;
    private RelativeLayout mEventPoster;
    private Button mChangePoster;

    private int mScreenWidth;

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
        mEventPoster = (RelativeLayout) findViewById(R.id.event_poster);
        mChangePoster = (Button) findViewById(R.id.change_poster);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        Log.i("MainActivity", "displayWidth  = " + size.x);
        Log.i("MainActivity", "displayHeight  = " + size.y);
    }

    private void setupView() {
        SimpleDateFormat sdf = new SimpleDateFormat("E, MMM dd, yyyy", Locale.ENGLISH);
        String currentDate = sdf.format(new Date());
        mEventDate.setHint(currentDate);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEventTime.setAdapter(adapter);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        mEventPoster.setBackground(decodeUri(selectedImage));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
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
