package stem.cis3086.uom.stem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by elise on 05/01/2017.
 */

public class HelperClass {
    public Bitmap getBitmapFromURL(String src) {
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


    public ArrayList<LessonPlans> getAllLessonPlans() {
        try {
            AsyncTask<Void, Void, ArrayList<LessonPlans>> aTask = new DBClass();
            return aTask.execute().get();
        }catch(InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
        return null;
    }
}
