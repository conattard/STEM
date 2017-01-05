package stem.cis3086.uom.stem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by elise on 05/01/2017.
 */

public class RecyclerViewImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<LessonPlans> lessonPlans;
    private ArrayList<String> urlsArrayList;

    private void extractImages() {
        try {
            for (LessonPlans lessonPlan : lessonPlans) {
                urlsArrayList.add(lessonPlan.getThumbnailUrl());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private Bitmap getBitmapFromURL(String src) {
        try {
            Bitmap x;

            HttpURLConnection connection = (HttpURLConnection) new URL(src).openConnection();
            connection.setRequestProperty("User-agent", "Mozilla/4.0");

            connection.connect();
            InputStream input = connection.getInputStream();

            x = BitmapFactory.decodeStream(input);
            return x;
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    RecyclerViewImagesAdapter(Context context){
        this.mContext = context;
    }

    public RecyclerViewImagesAdapter(Context context, ArrayList<LessonPlans> lessonPlans){
        this.mContext = context;
        this.lessonPlans = lessonPlans;
        urlsArrayList = new ArrayList<>();
        extractImages();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_image_layout, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ImageViewHolder){
            //Image image = imageResources[position];
            if(urlsArrayList!=null) {
                Bitmap bitmap = getBitmapFromURL(urlsArrayList.get(position));


                if (bitmap != null) {
                    ((ImageViewHolder) holder).imageView.setImageBitmap(bitmap);
                    ((ImageViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, "clicked picture in position: " + (position + 1), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return lessonPlans.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        private ImageViewHolder(View view){
            super(view);
            imageView = (ImageView)view.findViewById(R.id.recyclerImage);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }
}
