package stem.cis3086.uom.stem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by elise on 05/01/2017.
 */

public class LessonPlansAdapter extends BaseAdapter {

    Context context;
    int layoutResourceId;
    ArrayList<LessonPlans> lessonPlansArrayList;
    HelperClass helperClass = new HelperClass();



    public LessonPlansAdapter(Context context, int layResourceId, ArrayList<LessonPlans> data){

        this.context = context;
        this.layoutResourceId = layResourceId;
        this.lessonPlansArrayList = data;
    }


    @Override
    public int getCount() {
        return lessonPlansArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return lessonPlansArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LessonplanHolder lessonplanHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.search_item_layout, null);
            lessonplanHolder = new LessonplanHolder();
            lessonplanHolder.imageView = (ImageView) convertView.findViewById(R.id.searchImageView);
            lessonplanHolder.textView = (TextView)convertView.findViewById(R.id.searchTextView);
            convertView.setTag(lessonplanHolder);
        } else {
            lessonplanHolder = (LessonplanHolder)convertView.getTag();
        }

        LessonPlans lessonPlans = lessonPlansArrayList.get(position);
        lessonplanHolder.imageView.setImageBitmap(helperClass.getBitmapFromURL(lessonPlans.getThumbnailUrl()));
        lessonplanHolder.textView.setText(lessonPlans.getName());

        return convertView;

    }

    private static class LessonplanHolder{
        ImageView imageView;
        TextView textView;
    }
}
