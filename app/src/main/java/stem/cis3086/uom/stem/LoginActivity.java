package stem.cis3086.uom.stem;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity
{
    private String url = "http://stemapp.azurewebsites.net/Account/CheckPassword?username=";
    private boolean isSuccessful = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etUsername = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        final Button bLogin = (Button) findViewById(R.id.bRegister);

        final TextView registerLink = (TextView) findViewById(R.id.tvRegisterHere);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        registerLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                url = url + username + "&password=" + password;
                new LoginActivity.sendJsonData().execute();
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

    protected class sendJsonData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            RegisterActivity.ServiceHandler serviceHandler = new RegisterActivity.ServiceHandler();

            String jsonString = serviceHandler.makeServiceCall(url);
            if (jsonString.equals("\"True\"")){
                isSuccessful = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isSuccessful){
                Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(getApplicationContext(), "Login Unsuccessful", Toast.LENGTH_SHORT).show();
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
