package stem.cis3086.uom.stem;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity
{

    private String url = "http://stemapp.azurewebsites.net/Account/AppRegister?";
    private boolean isSuccessful = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText minAge = (EditText) findViewById(R.id.minAge);
        final EditText maxAge = (EditText) findViewById(R.id.maxAge);
        final EditText etName = (EditText) findViewById(R.id.etName);
        final EditText etSurname = (EditText) findViewById(R.id.etSurname);
        final EditText etSubject = (EditText) findViewById(R.id.subject);
        final EditText etUsername = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bRegister = (Button) findViewById(R.id.bRegister);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String name = etName.getText().toString();
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                final String surname = etSurname.getText().toString();
                final String subject = etSubject.getText().toString();
                final int minimumAge = Integer.parseInt(minAge.getText().toString());
                final int maximumAge = Integer.parseInt(maxAge.getText().toString());

                url = url + "email=" + username + "&password=" + password + "&firstName=" + name + "&lastName=" + surname + "&subject=" + subject + "&minAge=" + minimumAge +"&maxAge=" + maximumAge;
                new sendJsonData().execute();
            }
        });
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

    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    protected class sendJsonData extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            RegisterActivity.ServiceHandler serviceHandler = new RegisterActivity.ServiceHandler();

            String jsonString = serviceHandler.makeServiceCall(url);
            if (jsonString.equals("\"Registration successful\"")){
                isSuccessful = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isSuccessful){
                Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ShareActivity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(getApplicationContext(), "Registration Unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class ServiceHandler{
        public ServiceHandler(){}

        public String makeServiceCall(String url){
            return this.request(url);
        }

        public String request(String urlString){
            StringBuffer chain = new StringBuffer("");
            try{
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestProperty("User-Agent", " ");
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();

                BufferedReader rd  = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while((line = rd.readLine()) != null){
                    chain.append(line);
                }
            } catch (IOException e){
                e.printStackTrace();
            }
            return chain.toString();
        }

    }
}


