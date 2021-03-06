package stem.cis3086.uom.stem.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import de.hdodenhof.circleimageview.CircleImageView;
import stem.cis3086.uom.stem.AddPostActivity;
import stem.cis3086.uom.stem.CommentsActivity;
import stem.cis3086.uom.stem.R;

import static android.app.Activity.RESULT_OK;

public class ScienceFragment extends Fragment {

    private static final String ARG_ID = "argId";
    public static final int REQUEST_ADD_POST = 10;

    private String lessonId;

    private TextView helperTextView;
    private FloatingActionButton addPostFab;
    private RecyclerView recyclerView;

    public static ScienceFragment newInstance(String lessonId) {
        ScienceFragment fragment = new ScienceFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_science, container, false);

        // Find views
        findViews(rootView);

        // Get data
        getData();

        // Setup FAB
        addPostFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                intent.putExtra(AddPostActivity.EXTRA_LESSON_ID, lessonId);
                startActivityForResult(intent, REQUEST_ADD_POST);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    addPostFab.hide();
                else if (dy < 0)
                    addPostFab.show();
            }
        });

        return rootView;
    }

    private void findViews(View view){
        helperTextView = (TextView) view.findViewById(R.id.scienceHelperText);
        addPostFab = (FloatingActionButton) view.findViewById(R.id.scienceAddPostFab);
        recyclerView = (RecyclerView) view.findViewById(R.id.scienceRecyclerView);
    }

    private void getData() {
        String path = "http://stemapp.azurewebsites.net/Social/GetPostsByLessonPlanId?lessonPlanId=" + lessonId ;
        Ion.with(this)
                .load(path)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        // Show helper text view
                        helperTextView.setVisibility(result.size() > 0 ? View.GONE : View.VISIBLE);

                        // Setup recycler view
                        recyclerView.setAdapter(new PostsAdapter(result));
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_POST && resultCode == RESULT_OK){
            getData();
        }
    }

    private class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private JsonArray postsJsonArray;

        private PostsAdapter(JsonArray postsJsonArray) {
            this.postsJsonArray = postsJsonArray;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PostViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.view_post_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof PostViewHolder) {
                ((PostViewHolder) holder).bind(postsJsonArray.get(position).getAsJsonObject());
            }
        }

        @Override
        public int getItemCount() {
            return postsJsonArray.size();
        }

        private class PostViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView authorImageView;
            private TextView authorNameTextView;
            private TextView dateTextView;
            private TextView titleTextView;
            private TextView descriptionTextView;
            private ImageView imageView;
            private ImageButton commentsButton;

            private PostViewHolder(View itemView) {
                super(itemView);

                commentsButton = (ImageButton) itemView.findViewById(R.id.postCommentsButton);
                authorImageView = (CircleImageView) itemView.findViewById(R.id.postAuthorImageView);
                authorNameTextView = (TextView) itemView.findViewById(R.id.postAuthorName);
                dateTextView = (TextView) itemView.findViewById(R.id.postDate);
                descriptionTextView = (TextView) itemView.findViewById(R.id.postDescription);
                titleTextView = (TextView) itemView.findViewById(R.id.postTitle);
                imageView = (ImageView) itemView.findViewById(R.id.postThumbnailImageView);
            }

            private void bind(final JsonObject source) {
                // Set comment click handler
                commentsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = source.get("Id").getAsString();
                        Intent intent = new Intent(getActivity(), CommentsActivity.class);
                        intent.putExtra(CommentsActivity.EXTRA_POST_ID, id);
                        startActivity(intent);
                    }
                });

                // Get user data
                if(!source.get("Originator").isJsonNull()){
                    JsonObject author = source.getAsJsonObject("Originator");
                    authorNameTextView.setText(author.get("FullName").getAsString());
                }

                // Get data
                dateTextView.setText("1 day ago");
                titleTextView.setText(source.get("Title").getAsString());
                descriptionTextView.setText(source.get("Description").getAsString());

                // Load video thumbnail in image
                final String mediaUrl = source.get("MediaUrl").getAsString();
                final String videoId = mediaUrl.substring(mediaUrl.indexOf("=") + 1);
                final String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";

                Ion.with(imageView)
                        .placeholder(R.drawable.loading_resources_placeholder)
                        .error(R.drawable.no_resources_error)
                        .load(thumbnailUrl);

                // Open video on click
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mediaUrl));
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
