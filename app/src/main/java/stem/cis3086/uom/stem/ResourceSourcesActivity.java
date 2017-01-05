package stem.cis3086.uom.stem;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResourceSourcesActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_SOURCE = 10;
    public static final String EXTRA_RESOURCE_ID = "extraId";
    private String resourceId;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    public ResourceSourcesActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_sources);

        // Get id
        if (getIntent().hasExtra(EXTRA_RESOURCE_ID)){
            resourceId = getIntent().getStringExtra(EXTRA_RESOURCE_ID);
        }

        // Find views
        findViews();

        // Get data
        getData();

        // Setup FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResourceSourcesActivity.this, AddResourceSourceActivity.class);
                startActivityForResult(intent, REQUEST_ADD_SOURCE);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });

        // Setup toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.resourceSourcesToolbar);
        recyclerView = (RecyclerView) findViewById(R.id.resourceSourcesRecyclerView);
        fab = (FloatingActionButton) findViewById(R.id.resourceSourcesAddFab);
    }

    private void getData() {
        String path = "http://stemapp.azurewebsites.net/Social/GetResourceById?id=" + resourceId;
        Ion.with(this)
                .load(path)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        // Get lesson plan from array
                        JsonObject resource = result.get(0).getAsJsonObject();

                        // Get resources array
                        JsonArray sourcesArray = resource.getAsJsonArray("ResourceSources");

                        // Set title
                        toolbar.setTitle(resource.get("Name").getAsString());

                        // Setup recycler view
                        recyclerView.setAdapter(new SourcesAdapter(sourcesArray));
                        recyclerView.setLayoutManager(new LinearLayoutManager(ResourceSourcesActivity.this));
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_SOURCE && resultCode == RESULT_OK){
            getData();
        }
    }

    private class SourcesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private JsonArray sourcesJsonArray;

        private SourcesAdapter(JsonArray resourcesJsonArray) {
            this.sourcesJsonArray = resourcesJsonArray;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SourceViewHolder(LayoutInflater.from(ResourceSourcesActivity.this).inflate(R.layout.view_resource_source_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SourceViewHolder) {
                ((SourceViewHolder) holder).bind(sourcesJsonArray.get(position).getAsJsonObject());
            }
        }

        @Override
        public int getItemCount() {
            return sourcesJsonArray.size();
        }

        private class SourceViewHolder extends RecyclerView.ViewHolder {

            private TextView nameTextView;
            private TextView dateTextView;
            private TextView descriptionTextView;
            private TextView addressTextView;
            private ImageView mapImageView;
            private ImageButton shareButton;


            private SourceViewHolder(View itemView) {
                super(itemView);

                nameTextView = (TextView) itemView.findViewById(R.id.resourceSourceName);
                dateTextView = (TextView) itemView.findViewById(R.id.resourceSourceDate);
                descriptionTextView = (TextView) itemView.findViewById(R.id.resourceSourceDescription);
                addressTextView = (TextView) itemView.findViewById(R.id.resourceSourceAddress);
                mapImageView = (ImageView) itemView.findViewById(R.id.resourceSourceMapView);
                shareButton = (ImageButton) itemView.findViewById(R.id.resourceSourceShareButton);
            }

            private void bind(JsonObject source) {
                // Get user data
                if(!source.get("Originator").isJsonNull()){
                    JsonObject author = source.getAsJsonObject("Originator");
                    nameTextView.setText(author.get("FullName").getAsString());
                }

                final String name = source.get("Name").getAsString();
                dateTextView.setText("1 day ago");
                addressTextView.setText(name);
                descriptionTextView.setText(source.get("Description").getAsString());

                // Load map for nearest resource using Static Maps API
                final double latitude = source.get("Latitude").getAsDouble();
                final double longitude = source.get("Logitude").getAsDouble();

                StringBuilder stringBuilder = new StringBuilder("");

                stringBuilder.append("https://maps.googleapis.com/maps/api/staticmap?center=");
                stringBuilder.append(latitude);
                stringBuilder.append(",");
                stringBuilder.append(longitude);
                stringBuilder.append("&zoom=12&markers=color:red%7Clabel:S%7C");
                stringBuilder.append(latitude);
                stringBuilder.append(",");
                stringBuilder.append(longitude);
                stringBuilder.append("&size=1000x500&key=AIzaSyBcI5WQUbu83dPwYLleKv9dur22-qkcHvI");

                Ion.with(mapImageView)
                        .placeholder(R.drawable.loading_resources_placeholder)
                        .error(R.drawable.no_resources_error)
                        .load(stringBuilder.toString());

                // Open navigation on click
                mapImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }
                    }
                });

                // Open share on click
                shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ResourceSourcesActivity.this, ShareActivity.class);
                        intent.putExtra(ShareActivity.EXTRA_LONGITUDE, (float) longitude);
                        intent.putExtra(ShareActivity.EXTRA_LATITUDE, (float) latitude);
                        intent.putExtra(ShareActivity.EXTRA_RESOURCE_NAME, toolbar.getTitle());
                        intent.putExtra(ShareActivity.EXTRA_ADDRESS, name);

                        startActivity(intent);
                    }
                });
            }
        }
    }
}
