package stem.cis3086.uom.stem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ChoiceActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        final TextView tvParent = (TextView) findViewById(R.id.tvParent);
        final TextView tvTeacher = (TextView) findViewById(R.id.tvTeacher);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent registerIntent = new Intent(ChoiceActivity.this, RegisterActivity.class);

        tvTeacher.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ChoiceActivity.this.startActivity(registerIntent);
            }
        });

        tvParent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ChoiceActivity.this.startActivity(registerIntent);
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
}
