package stem.cis3086.uom.stem;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by elise on 05/01/2017.
 */

public class DBClass extends AsyncTask<Void, Void, ArrayList<LessonPlans>> {

    InputStream inputStream = null;
    InputStreamReader inputStreamReader = null;
    String result = "";
    ArrayList<String> stringArrayList = new ArrayList<>();
    ArrayList<LessonPlans> lessonPlansArrayList = new ArrayList<>();
    TextView textView;
    JSONObject jsonObject = null;
    JSONArray jsonArray = null;
    LessonPlans lessonPlan = null;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<LessonPlans> doInBackground(Void... params) {
        String stringUrl = "http://stemapp.azurewebsites.net/LessonPlans/GetAllLessonPlans";

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        try{
            URL url = new URL(stringUrl) ;
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";


            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");

            }
            jsonArray = new JSONArray(stringBuffer.toString());
            for(int i = 0; i < jsonArray.length(); i++){
                jsonObject = jsonArray.getJSONObject(i);
                JSONArray jsonArray2 = jsonObject.getJSONArray("TagHelpers");
                ArrayList<String> tags = new ArrayList<>();
                for(int j = 0; j < jsonArray2.length(); j++){
                    JSONObject jsonObject2 = jsonArray2.getJSONObject(j);
                    if(jsonObject2.getBoolean("TagSelected")){
                        tags.add(jsonObject2.getString("TagName"));
                    }
                }
                lessonPlan = new LessonPlans(jsonObject.getInt("Id"),jsonObject.getString("Name"), jsonObject.getString("ThumbnailUrl"),
                        jsonObject.getString("Summary"), jsonObject.getInt("MinGrade"),jsonObject.getInt("MaxGrade"),jsonObject.getString("Subject"),
                        jsonObject.getString("Keywords"), jsonObject.getString("ShortDescription"), jsonObject.getInt("Votes"), jsonObject.getString("FileUrl"),
                        jsonObject.getInt("RecommendedAge"), jsonObject.getString("LessonFocus"), jsonObject.getString("LessonSynopsis"), jsonObject.getString("Objectives"),
                        jsonObject.getString("LearnerOutcomes"), jsonObject.getString("LessonActivities"), jsonObject.getString("Aligment"),jsonObject.getString("RecommendeReading"),
                        jsonObject.getString("OptionalActivities"), jsonObject.getString("LessonProcedure"), jsonObject.getString("TimeNeeded"), jsonObject.getString("InternetConnections"),
                        jsonObject.getString("ExtensionActivity"), jsonObject.getString("SafetyNotes"),jsonObject.getString("TeacherResources"), jsonObject.getString("StudentNotes"),
                        jsonObject.getString("StudentWorksheets"), tags);

                lessonPlansArrayList.add(lessonPlan);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return lessonPlansArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<LessonPlans> lessonPlans) {
        super.onPostExecute(lessonPlans);
    }

}
