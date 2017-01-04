package stem.cis3086.uom.stem.fragments;


import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import stem.cis3086.uom.stem.LessonDetailActivity;
import stem.cis3086.uom.stem.R;

public class ResourcesFragment extends Fragment {

    private static final String ARG_ID = "argId";
    private String lessonId;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_resources, container, false);

        findViews(rootView);
        getData();

        return rootView;
    }

    private void findViews(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.resourcesRecyclerView);
    }

    private void getData(){
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

                        // Setup recycler view
                        recyclerView.setAdapter(new ResourcesAdapter(resourcesArray));
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                });
    }

    private class ResourcesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

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
            if (holder instanceof ResourceViewHolder){
                ((ResourceViewHolder) holder).bind(resourcesJsonArray.get(position).getAsJsonObject());
            }
        }

        @Override
        public int getItemCount() {
            return resourcesJsonArray.size();
        }

        private class ResourceViewHolder extends RecyclerView.ViewHolder{

            private TextView titleTextView;
            private TextView isStudentTextView;
            private TextView submissionCountTextView;
            private TextView nearestLocationButtonTextView;
            private TextView allSubmissionsButtonTextView;
            private ImageView mapImageView;


            public ResourceViewHolder(View itemView) {
                super(itemView);

                titleTextView = (TextView) itemView.findViewById(R.id.resourceItemTitle);
                isStudentTextView = (TextView) itemView.findViewById(R.id.resourceItemIsStudent);
                submissionCountTextView  = (TextView) itemView.findViewById(R.id.resourceItemSubmissions);
                nearestLocationButtonTextView  = (TextView) itemView.findViewById(R.id.resourceItemNearestLocation);
                allSubmissionsButtonTextView = (TextView) itemView.findViewById(R.id.resourceItemAllSubmissions);
                mapImageView = (ImageView) itemView.findViewById(R.id.resourceItemMapView);
            }

            public void bind(JsonObject resource){
                // Set data
                JsonArray resourceSources = resource.getAsJsonArray("ResourceSources");

                titleTextView.setText(resource.get("Name").getAsString());
                isStudentTextView.setText(resource.get("IsStudentResource").getAsBoolean() ? "Student Resource" : "Educator Resource");
                submissionCountTextView.setText(resourceSources.size() == 1 ? resourceSources.size() + " submission" : resourceSources.size() + " submissions");

                if (resourceSources.size() == 0){
                    allSubmissionsButtonTextView.setVisibility(View.GONE);
                    nearestLocationButtonTextView.setText("Submit resource");

                    mapImageView.setImageResource(R.drawable.no_resources_error);
                } else  {
                    allSubmissionsButtonTextView.setVisibility(View.VISIBLE);
                    nearestLocationButtonTextView.setText("Nearest location");

                    JsonObject nearestResource = getNearestResource(resourceSources);
                    double latitude = nearestResource.get("sourceLatitude").getAsDouble();
                    double longitude = nearestResource.get("sourceLongitude").getAsDouble();

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

            private JsonObject getNearestResource(JsonArray resourceSources){
                JsonObject nearest = null;
                for (int i = 0; i < resourceSources.size(); i++){
                    JsonObject resource = resourceSources.get(i).getAsJsonObject();
                    if (distanceToResource(resource) < distanceToResource(nearest) || nearest == null){
                        nearest = resource;
                    }
                }
                return  nearest;
            }

            private float distanceToResource(JsonObject resource){
                // TODO: Calculate distance to resource
                return 0f;
            }
        }
    }

}
