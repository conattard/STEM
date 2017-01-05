package stem.cis3086.uom.stem;

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

import java.util.Date;

public class AddPostActivity extends AppCompatActivity {


    public static final String EXTRA_LESSON_ID = "argId";
    private final String TAG = this.getClass().getSimpleName();

    private String lessonId;

    private Button submitButton;
    private AppCompatEditText titleEditText;
    private AppCompatEditText descriptionEditText;
    private AppCompatEditText urlEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        findViews();

        if (getIntent().hasExtra(EXTRA_LESSON_ID)){
            lessonId = getIntent().getStringExtra(EXTRA_LESSON_ID);
        }

        // Setup add button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void findViews(){
        submitButton = (Button) findViewById(R.id.addPostSubmitButton);
        titleEditText = (AppCompatEditText) findViewById(R.id.addPostTitle);
        descriptionEditText = (AppCompatEditText) findViewById(R.id.addPostDescription);
        urlEditText = (AppCompatEditText) findViewById(R.id.addPostMediaUrl);
    }

    private void submit(){
        // Validate source
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String url = urlEditText.getText().toString().trim();

        boolean isValid = true;

        if (title.isEmpty()){
            isValid = false;
            titleEditText.setError("Title is required");
        }


        if (description.isEmpty()){
            isValid = false;
            descriptionEditText.setError("Descripton is required");
        }


        if (url.isEmpty()){
            isValid = false;
            urlEditText.setError("Media URL is required");
        }

        if (isValid){
            // Update submit button
            submitButton.setText("Posting...");

            // Submit source to web api
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("stemapp.azurewebsites.net")
                    .appendPath("Social")
                    .appendPath("CreatePost")
                    .appendQueryParameter("lessonPlanId", lessonId)
                    .appendQueryParameter("userId", STEM.userId)
                    .appendQueryParameter("mediaUrl", url)
                    .appendQueryParameter("title", title)
                    .appendQueryParameter("description", description)
                    .appendQueryParameter("date", new Date().toString());

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
                    new MaterialDialog.Builder(AddPostActivity.this)
                            .title("Submit source")
                            .content("Failed to submit source: " + error.getMessage())
                            .positiveText("Continue")
                            .build()
                            .show();
                }
            });

            RequestQueue volleyRequestQueue = Volley.newRequestQueue(AddPostActivity.this);
            volleyRequestQueue.add(postRequest);
        }
    }
}
