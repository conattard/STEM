package stem.cis3086.uom.stem.fragments;


import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import stem.cis3086.uom.stem.AddResourceSourceActivity;
import stem.cis3086.uom.stem.LessonDetailActivity;
import stem.cis3086.uom.stem.R;
import stem.cis3086.uom.stem.ResourceSourcesActivity;

import static android.app.Activity.RESULT_OK;

public class ResourcesFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String ARG_ID = "argId";
    private static final int REQUEST_ADD_SOURCE = 10;

    private String lessonId;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;

    private TextView helperTextView;
    private RecyclerView recyclerView;

    public ResourcesFragment() {
        // Required empty public constructor
    }

    public static ResourcesFragment newInstance(String lessonId) {
        ResourcesFragment fragment = new ResourcesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, lessonId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lessonId = getArguments().getString(ARG_ID);
        }

        // Connect to Google API client
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(ResourcesFragment.this)
                .addOnConnectionFailedListener(ResourcesFragment.this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_resources, container, false);

        findViews(rootView);
        getGpsLocation();
        getData();

        return rootView;
    }

    private void findViews(View view) {
        helperTextView = (TextView) view.findViewById(R.id.resourcesHelperText);
        recyclerView = (RecyclerView) view.findViewById(R.id.resourcesRecyclerView);
    }

    private void getData() {
        String path = LessonDetailActivity.LESSON_PATH + lessonId;
        Ion.with(getActivity())
                .load(path)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        // Get lesson plan from array
                        JsonObject lessonPlan = result.get(0).getAsJsonObject();

                        // Get resources array
                        JsonArray resourcesArray = lessonPlan.getAsJsonArray("Resources");

                        // Show helper text view
                        helperTextView.setVisibility(resourcesArray.size() > 0 ? View.GONE : View.VISIBLE);

                        // Setup recycler view
                        recyclerView.setAdapter(new ResourcesAdapter(resourcesArray));
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                });
    }

    private void getGpsLocation() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // Get last known gps locations
                        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // Show dialog
                        new MaterialDialog.Builder(getActivity())
                                .theme(Theme.LIGHT)
                                .title("Permission denied")
                                .content("GPS permission is required to show nearest resources.")
                                .positiveText("Continue")
                                .build()
                                .show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, final PermissionToken token) {
                        // Show dialog
                        new MaterialDialog.Builder(getActivity())
                                .theme(Theme.LIGHT)
                                .title("GPS permission")
                                .content("GPS permission is required to show nearest resources.")
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
                }).check();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Ask for permission and get location
        getGpsLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_SOURCE && resultCode == RESULT_OK) {
            getData();
        }
    }

    private class ResourcesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private JsonArray resourcesJsonArray;

        public ResourcesAdapter(JsonArray resourcesJsonArray) {
            this.resourcesJsonArray = resourcesJsonArray;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ResourceViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.view_resource_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ResourceViewHolder) {
                ((ResourceViewHolder) holder).bind(resourcesJsonArray.get(position).getAsJsonObject());
            }
        }

        @Override
        public int getItemCount() {
            return resourcesJsonArray.size();
        }

        private class ResourceViewHolder extends RecyclerView.ViewHolder {

            private TextView titleTextView;
            private TextView isStudentTextView;
            private TextView submissionCountTextView;
            private TextView nearestLocationButtonTextView;
            private TextView allSubmissionsButtonTextView;
            private ImageView mapImageView;


            private ResourceViewHolder(View itemView) {
                super(itemView);

                titleTextView = (TextView) itemView.findViewById(R.id.resourceItemTitle);
                isStudentTextView = (TextView) itemView.findViewById(R.id.resourceItemIsStudent);
                submissionCountTextView = (TextView) itemView.findViewById(R.id.resourceItemSubmissions);
                nearestLocationButtonTextView = (TextView) itemView.findViewById(R.id.resourceItemNearestLocation);
                allSubmissionsButtonTextView = (TextView) itemView.findViewById(R.id.resourceItemAllSubmissions);
                mapImageView = (ImageView) itemView.findViewById(R.id.resourceItemMapView);


            }

            private void bind(JsonObject resource) {
                final String resourceId = String.valueOf(resource.get("Id").getAsInt());

                // Set data
                JsonArray resourceSources = resource.getAsJsonArray("ResourceSources");
                titleTextView.setText(resource.get("Name").getAsString());
                isStudentTextView.setText(resource.get("IsStudentResource").getAsBoolean() ? "Student Resource" : "Educator Resource");
                submissionCountTextView.setText(resourceSources.size() == 1 ? resourceSources.size() + " submission" : resourceSources.size() + " submissions");

                if (resourceSources.size() == 0) {
                    // Update view to reflect no sources
                    allSubmissionsButtonTextView.setVisibility(View.GONE);
                    nearestLocationButtonTextView.setText("Submit resource");
                    mapImageView.setImageResource(R.drawable.no_resources_error);

                    // Set onclick handler
                    nearestLocationButtonTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), AddResourceSourceActivity.class);
                            startActivityForResult(intent, REQUEST_ADD_SOURCE);

                        }
                    });
                } else {
                    // Get nearest resource
                    JsonObject nearestResource = getNearestResource(resourceSources);
                    final double latitude = nearestResource.get("Latitude").getAsDouble();
                    final double longitude = nearestResource.get("Logitude").getAsDouble();

                    // Update buttons
                    allSubmissionsButtonTextView.setVisibility(View.VISIBLE);
                    nearestLocationButtonTextView.setText("Nearest location");

                    nearestLocationButtonTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(mapIntent);
                            }
                        }
                    });

                    // Set onclick handler
                    allSubmissionsButtonTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), ResourceSourcesActivity.class);
                            intent.putExtra(ResourceSourcesActivity.EXTRA_RESOURCE_ID, String.valueOf(resourceId));
                            startActivity(intent);
                        }
                    });

                    // Load map for nearest resource using Static Maps API
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

                    //"https://maps.googleapis.com/maps/api/staticmap?center=35.891746,14.442517&zoom=12&markers=color:red%7Clabel:S%7C35.891746,14.442517&size=1000x500&key=AIzaSyBcI5WQUbu83dPwYLleKv9dur22-qkcHvI";
                    Ion.with(mapImageView)
                            .placeholder(R.drawable.loading_resources_placeholder)
                            .error(R.drawable.no_resources_error)
                            .load(stringBuilder.toString());
                }
            }

            private JsonObject getNearestResource(JsonArray resourceSources) {
                JsonObject nearest = null;
                for (int i = 0; i < resourceSources.size(); i++) {
                    JsonObject source = resourceSources.get(i).getAsJsonObject();
                    if (nearest == null || distanceToResource(source) < distanceToResource(nearest)) {
                        nearest = source;
                    }
                }
                return nearest;
            }

            private double distanceToResource(JsonObject source) {
                // Return if current location is not found
                if (currentLocation == null) {
                    return Double.MAX_VALUE;
                }

                // Create resource location
                double latitude = source.get("Latitude").getAsDouble();
                double longitude = source.get("Logitude").getAsDouble();
                Location resourceLocation = new Location(LocationManager.GPS_PROVIDER);
                resourceLocation.setLatitude(latitude);
                resourceLocation.setLongitude(longitude);

                // Return distance
                return currentLocation.distanceTo(resourceLocation);
            }
        }
    }

}
