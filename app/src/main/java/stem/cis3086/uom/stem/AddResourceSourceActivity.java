package stem.cis3086.uom.stem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class AddResourceSourceActivity extends AppCompatActivity {

    private final int PLACE_PICKER_REQUEST = 10;
    private final String TAG = this.getClass().getSimpleName();
    public static final String EXTRA_RESOURCE_ID = "extraId";

    private String resourceId;
    private Place selectedPlace;

    private Button submitButton;
    private Button selectLocationButton;
    private AppCompatEditText priceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource_source);

        findViews();

        // Get id
        if (getIntent().hasExtra(EXTRA_RESOURCE_ID)){
            resourceId = getIntent().getStringExtra(EXTRA_RESOURCE_ID);
        } else {
            resourceId = "4";
        }

        // Setup buttons
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPlacePicker();
            }
        });
    }

    private void findViews(){
        submitButton = (Button) findViewById(R.id.addSourceSubmitButton);
        selectLocationButton = (Button) findViewById(R.id.addSourceLocationButton);
        priceTextView = (AppCompatEditText) findViewById(R.id.addSourcePrice);
    }

    private void launchPlacePicker() {
        Dexter.withActivity(AddResourceSourceActivity.this)
                .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // Launch place picker
                        try {
                            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                            startActivityForResult(intentBuilder.build(AddResourceSourceActivity.this), PLACE_PICKER_REQUEST);
                        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e){
                            Log.e(TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // Show dialog
                        new MaterialDialog.Builder(AddResourceSourceActivity.this)
                                .title("Permission denied")
                                .content("GPS permission is required to select location.")
                                .positiveText("Continue")
                                .build()
                                .show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, final PermissionToken token) {
                        // Show dialog
                        new MaterialDialog.Builder(AddResourceSourceActivity.this)
                                .title("GPS permission")
                                .content("GPS permission is required to select location.")
                                .positiveText("Grant Permission")
                                .negativeText("Ignore")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        token.continuePermissionRequest();
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                        token.cancelPermissionRequest();
                                    }
                                })
                                .build()
                                .show();
                    }
                })
                .check();

    }

    private void submit(){
        // Validate source
        String price = priceTextView.getText().toString().trim();
        boolean isValid = true;

        if (price.isEmpty()){
            isValid = false;
            priceTextView.setError("Price is required");
        }

        if (selectedPlace == null){
            isValid = false;
            selectLocationButton.setBackgroundResource(R.color.red);
            selectLocationButton.setText("Location is required");
        }

        if (isValid){
            // Update submit button
            submitButton.setText("Saving...");
            // Submit source to web api
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("stemapp.azurewebsites.net")
                    .appendPath("Social")
                    .appendPath("CreateResourceSource")
                    .appendQueryParameter("userId", STEM.userId)
                    .appendQueryParameter("resourceId", resourceId)
                    .appendQueryParameter("sourceName", selectedPlace.getName().toString())
                    .appendQueryParameter("sourceDescription", "€" + price)
                    .appendQueryParameter("sourceLongitude", String.valueOf(selectedPlace.getLatLng().longitude))
                    .appendQueryParameter("sourceLatitude", String.valueOf(selectedPlace.getLatLng().latitude));

            StringRequest postRequest = new StringRequest(Request.Method.POST, builder.build().toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, response);
                    setResult(RESULT_OK);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    submitButton.setText("Submit");
                    new MaterialDialog.Builder(AddResourceSourceActivity.this)
                            .title("Submit source")
                            .content("Failed to submit source: " + error.getMessage())
                            .positiveText("Continue")
                            .build()
                            .show();
                }
            });

            RequestQueue volleyRequestQueue = Volley.newRequestQueue(AddResourceSourceActivity.this);
            volleyRequestQueue.add(postRequest);

            //http://stemapp.azurewebsites.net/Social/CreateResourceSource?userId=1&resourceId=4&sourceName=Scan&sourceDescription=eur22&sourceLongitude=24.14&sourceLatitude=53.134
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK){
            selectedPlace = PlacePicker.getPlace(this, data);
            selectLocationButton.setText(selectedPlace.getName());
            Log.d(TAG, String.format("Place: %s", selectedPlace.getName()));
        }
    }
}
