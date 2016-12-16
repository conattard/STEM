package stem.cis3086.uom.stem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button bGoogle = (Button) findViewById(R.id.bGoogle);
        final Button bFacebook = (Button) findViewById(R.id.bFacebook);
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
                Intent signupIntent = new Intent(MainActivity.this, ChoiceActivity.class);
                MainActivity.this.startActivity(signupIntent);
            }
        });

    }
}
