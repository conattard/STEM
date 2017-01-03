package stem.cis3086.uom.stem;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;


/**
 * Created by SterlingRyan on 06/12/2016.
 */

public class ShareActivity extends AppCompatActivity {
    private String[] socialMediaOptions = new String[]{"Facebook", "Google+", "Twitter"};
    private MaterialBetterSpinner spinner;
    private static final String TAG = "ShareAcitivity";

    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        //Toolbar customization
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set Up Home Button color to white
        final Drawable upArrow = this.getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        // Set up Spinner Toolbar
        spinner = (MaterialBetterSpinner) myToolbar.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, socialMediaOptions);
        spinner.setAdapter(adapter);

        //Set Edit Text
        editText = (EditText) findViewById(R.id.content);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbuttons, menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sendPost();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Sends a post to the social platform selected
    private void sendPost(){
//        Bundle extras = getIntent().getExtras();
//        String resourcesType = extras.getString("resourcesType");
//        Float latitude = extras.getFloat("latitude");
//        Float longitude = extras.getFloat("longitude");
        switch (spinner.getText().toString()){
            case "Facebook":
                postOnFacebook("STEM Test", Float.valueOf((float) 35.890030), Float.valueOf((float) 14.431138));
                break;
            case "Google+":
                break;
            case "Twitter":
                break;
            default: finish();
        }
    }

    private void postOnFacebook(String resourcesType , Float latitude, Float longitude){

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle(resourcesType)
                .setContentDescription(editText.getText().toString())
                .setContentUrl(Uri.parse("http://maps.google.com/?q="+ latitude.toString() +"," + longitude.toString()))
                .build();

        ShareApi.share(content, null);
        Toast.makeText(this, "Post Successful",Toast.LENGTH_SHORT).show();
    }
}
