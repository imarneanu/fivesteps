package com.imarneanu.fivesteps;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    // Thu, Apr 28, 2016
    private final static String REGEX_TIME = "[M|T|W|F|S][a-z]{2},\\s[J|F|M|A|J|S|O|N|D][a-z]{2}\\s\\d{1,2},\\s\\d{4}";

    private EditText mEventTitle;
    private EditText mEventDate;
    private Spinner mEventTime;
    private TextView mEventLocation;
    private Button mPickLocation;
    private RelativeLayout mEventPoster;
    private Button mChangePoster;
    private EditText mEventAgenda;

    private Uri mSelectedPosterUri;
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
        mEventTitle = (EditText) findViewById(R.id.event_title);
        mEventDate = (EditText) findViewById(R.id.event_date);
        mEventTime = (Spinner) findViewById(R.id.event_time);
        mEventLocation = (TextView) findViewById(R.id.event_location);
        mPickLocation = (Button) findViewById(R.id.pick_location);
        mEventPoster = (RelativeLayout) findViewById(R.id.event_poster);
        mChangePoster = (Button) findViewById(R.id.change_poster);
        mEventAgenda = (EditText) findViewById(R.id.event_agenda);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
    }

    private void setupView() {
        mEventDate.setHint(getCurrentDate());

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
                    mSelectedPosterUri = data.getData();
                    try {
                        mEventPoster.setBackground(decodeUri(mSelectedPosterUri));
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

    public void sendEvent(View view) {
        if (validateData()) {
            sendEmail();
        }
    }

    private boolean validateData() {
        if (TextUtils.isEmpty(mEventTitle.getText())) {
            Toast.makeText(this, "Please set event title", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(mEventDate.getText())) {
            Toast.makeText(this, "Please set event date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!mEventDate.getText().toString().matches(REGEX_TIME)) {
            Toast.makeText(this, "Please use the " + getCurrentDate() + " format", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(mEventLocation.getText())) {
            Toast.makeText(this, "Please set event location", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(mEventAgenda.getText())) {
            mEventAgenda.setText(getText(R.string.event_agenda_to_be_added));
        }
        return true;
    }

    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "HOT NEW EVENT");
        intent.putExtra(Intent.EXTRA_TEXT, getEventBody());
        if (mSelectedPosterUri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, mSelectedPosterUri);
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private Spanned getEventBody() {
        String location = mEventLocation.getText().toString().replace("\n", "<br>");
        String agenda = mEventAgenda.getText().toString().replace("\n", "<br>");
        return Html.fromHtml(new StringBuilder()
                .append("<h1>").append(mEventTitle.getText()).append("</h1>")
                .append("<p><b>Location: </b>").append(mEventDate.getText())
                .append(", ").append(mEventTime.getSelectedItem().toString())
                .append("<br>").append(location).append("</p>")
                .append("<p><b>Agenda: </b><br>").append(agenda)
                .toString());
//        return mEventTitle.getText()
//                + "\n\nLocation: " + mEventDate.getText() + ", " + mEventTime.getSelectedItem().toString()
//                + "\n\t\t" + mEventLocation.getText()
//                + "\nAgenda:" + mEventAgenda.getText();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("E, MMM dd, yyyy", Locale.ENGLISH);
        return sdf.format(new Date());
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
