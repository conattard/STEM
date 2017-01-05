package stem.cis3086.uom.stem;

import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    public static final String EXTRA_POST_ID = "extraId";
    private String postId;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // Get id
        if (getIntent().hasExtra(EXTRA_POST_ID)){
            postId = getIntent().getStringExtra(EXTRA_POST_ID);
        } else {
            postId = "1";
        }

        // Find views
        findViews();

        // Get data
        getData();

        // Setup FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new MaterialDialog.Builder(CommentsActivity.this)
                    .theme(Theme.LIGHT)
                    .title("Add comment")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("Comment", "", false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            // Do something
                            submitComment(input.toString().trim());
                        }
                    }).show();
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
        toolbar = (Toolbar) findViewById(R.id.commentsToolbar);
        recyclerView = (RecyclerView) findViewById(R.id.commentsRecyclerView);
        fab = (FloatingActionButton) findViewById(R.id.commentsAddCommentFab);
    }

    private void getData() {
        String path = "http://stemapp.azurewebsites.net/Social/GetCommentsByPostId?postId=" + postId;
        Ion.with(this)
                .load(path)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        // Setup recycler view
                        recyclerView.setAdapter(new CommentsAdapter(result));
                        recyclerView.setLayoutManager(new LinearLayoutManager(CommentsActivity.this));
                    }
                });
    }

    private void submitComment(String comment){
        // Submit source to web api
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("stemapp.azurewebsites.net")
                .appendPath("Social")
                .appendPath("CreateComment")
                .appendQueryParameter("userId", "0")
                .appendQueryParameter("postId", postId)
                .appendQueryParameter("myComment", comment)
                .appendQueryParameter("date", new Date().toString());

        StringRequest postRequest = new StringRequest(Request.Method.POST, builder.build().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                getData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new MaterialDialog.Builder(CommentsActivity.this)
                        .title("Submit source")
                        .content("Failed to submit source: " + error.getMessage())
                        .positiveText("Continue")
                        .build()
                        .show();
            }
        });

        RequestQueue volleyRequestQueue = Volley.newRequestQueue(CommentsActivity.this);
        volleyRequestQueue.add(postRequest);
    }

    private class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private JsonArray postsJsonArray;

        private CommentsAdapter(JsonArray postsJsonArray) {
            this.postsJsonArray = postsJsonArray;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CommentViewHolder(LayoutInflater.from(CommentsActivity.this).inflate(R.layout.view_comment_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof CommentViewHolder) {
                ((CommentViewHolder) holder).bind(postsJsonArray.get(position).getAsJsonObject());
            }
        }

        @Override
        public int getItemCount() {
            return postsJsonArray.size();
        }

        private class CommentViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView authorImageView;
            private TextView authorNameTextView;
            private TextView dateTextView;
            private TextView commentTextView;


            private CommentViewHolder(View itemView) {
                super(itemView);

                authorImageView = (CircleImageView) itemView.findViewById(R.id.commentAuthorImageView);
                authorNameTextView = (TextView) itemView.findViewById(R.id.commentAuthorName);
                dateTextView = (TextView) itemView.findViewById(R.id.commentDate);
                commentTextView = (TextView) itemView.findViewById(R.id.commentContent);
            }

            private void bind(JsonObject source) {
                // Get user data
                authorNameTextView.setText("Tony Stark");
                dateTextView.setText("1 day ago");
                commentTextView.setText(source.get("MyComment").getAsString());
            }
        }
    }

}
