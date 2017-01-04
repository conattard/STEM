package stem.cis3086.uom.stem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by YranRiahi on 30/11/2016.
 */

public class RegisterRequest extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        Button submitBtn = (Button) findViewById(R.id.submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShareActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
    }
}
