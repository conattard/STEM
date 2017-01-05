package stem.cis3086.uom.stem;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;


public class MainActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener
{
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private CallbackManager callbackManager;
    private String email = "";
    private String urlString = "";
    private boolean isGoogle = false;
    private boolean isFacebook= false;
    private boolean isRegistered = false;
    private GoogleSignInAccount acct;
    private Intent data;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);



        final SignInButton bGoogle = (SignInButton) findViewById(R.id.bGoogle);
        bGoogle.setSize(SignInButton.SIZE_STANDARD);
        final Button bSignUp = (Button) findViewById(R.id.bSignUp);

        final TextView loginLink = (TextView) findViewById(R.id.tvSignIn);

        loginLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(loginIntent);
            }
        });

        bSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent signupIntent = new Intent(MainActivity.this, RegisterActivity.class);
                MainActivity.this.startActivity(signupIntent);
            }
        });

        bGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });


        LoginButton bFacebook = (LoginButton) findViewById(R.id.bFacebook);
        if(AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
        bFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToFacebook();
            }
        });

    }

    private void loginToFacebook(){
        callbackManager = CallbackManager.Factory.create();
        LoginButton bFacebook = (LoginButton) findViewById(R.id.bFacebook);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().logInWithPublishPermissions(
                this,
                Arrays.asList("publish_actions"));
        bFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    email = object.getString("email");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Log.v(TAG,"Login Successful");
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,error.toString());
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
            this.data = data;
            Bundle bundle = data.getExtras();
            urlString = "http://stemapp.azurewebsites.net/Account/CheckPassword?username=" + email + "&password=123";
            isFacebook = true;
            new sendJsonData().execute();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void googleSignIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });

            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully.
            acct = result.getSignInAccount();
            email = acct.getEmail();
            urlString = "http://stemapp.azurewebsites.net/Account/CheckPassword?username=" + email + "&password=123";
            isGoogle = true;
            new sendJsonData().execute();
        }
    }

    protected class sendJsonData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            MainActivity.ServiceHandler serviceHandler = new MainActivity.ServiceHandler();
            isRegistered = serviceHandler.makeServiceCall(urlString);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isGoogle){
                if(isRegistered){
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    intent.putExtra("Google Account", acct);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),RegisterRequest.class);
                    intent.putExtra("Google Account", acct);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            }
            else if (isFacebook){
                if(isRegistered){
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    intent.putExtra("Facebook Account", data);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), RegisterRequest.class);
                    intent.putExtra("Facebook Account", data);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            }
        }
    }

    public static class ServiceHandler{
        public ServiceHandler(){}

        public boolean makeServiceCall(String url){
            return this.isEmailAlreadyRegistered(url);
        }

        private boolean isEmailAlreadyRegistered(String urlString){
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
            if(chain.toString().equals("\"False\"")){
                return true;
            }
            else{
                return false;
            }
        }

    }


}
