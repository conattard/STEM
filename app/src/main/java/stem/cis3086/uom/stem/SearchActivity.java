package stem.cis3086.uom.stem;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

public class SearchActivity extends AppCompatActivity {

    private TextView textView;
    private ListView listView;
    JSONObject jsonObject = null;
    JSONArray jsonArray = null;
    String filename = "preference10.txt";
    HashMap<String, Integer> hashMap = new HashMap<>();
    HashMap<Integer, LessonPlans> q = new HashMap<>();
    AsyncTask<Void, Void, ArrayList<LessonPlans>> aTask = new DBClass();
    ArrayList<LessonPlans> uLessonPlans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //textView = (TextView) findViewById(R.id.text);
        listView = (ListView) findViewById(R.id.list);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            HashMap<Integer, LessonPlans> lessonPlansMap = search(query);

            TreeMap<Integer, LessonPlans> treeMap = new TreeMap<>(lessonPlansMap);
            NavigableMap<Integer, LessonPlans> navigableMap = treeMap.descendingMap();
            Set<Integer> set = navigableMap.keySet();
            Collection<LessonPlans> collection = navigableMap.values();

            if (lessonPlansMap.isEmpty()) {
                Toast.makeText(SearchActivity.this, "There are no lesson plans", Toast.LENGTH_SHORT).show();
            } else {
                for (int num :
                        set) {

                    System.out.println(num);
                }
                ArrayList<LessonPlans> lessonPlans = new ArrayList<>();
                if (lessonPlansMap.size() > 5) {

                    for (Iterator<LessonPlans> iterator = collection.iterator(); iterator.hasNext(); ) {
                        lessonPlans.add(iterator.next());
                    }
                } else {
                    lessonPlans = searchNoPreference(query);
                }
                LessonPlansAdapter lessonPlansAdapter = new LessonPlansAdapter(this, R.layout.search_item_layout, lessonPlans);

                listView.setAdapter(lessonPlansAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        jsonArray = ReadFromFile(filename);
                        JSONArray newArray = new JSONArray();
                        String category = "";
                        int count = 0;
                        try {
                            boolean first = false;
                            if (jsonArray.length() == 0) {
                                first = true;
                                LessonPlans lp = (LessonPlans) listView.getItemAtPosition(position);
                                ArrayList<String> tags2 = lp.getTags();
                                for (int k = 0; k < tags2.size(); k++) {

                                    JSONObject object = new JSONObject();
                                    object.put("Category", tags2.get(k));
                                    object.put("Count", 1);
                                    newArray.put(object);
                                }
                            }
                            ArrayList<String> tags1;
                            LessonPlans lp = (LessonPlans) listView.getItemAtPosition(position);
                            tags1 = (ArrayList<String>) lp.getTags().clone();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                boolean matched = false;
                                boolean tagsZero = false;
                                JSONObject obj = jsonArray.getJSONObject(i);
                                category = obj.getString("Category");
                                count = obj.getInt("Count");

                                if (tags1.size() == 0) {
                                    tagsZero = true;
                                }
                                for (int j = 0; j < tags1.size(); j++) {
                                    if (tags1.get(j).toLowerCase().contains(category.toLowerCase())) {
                                        count = count + 1;
                                        matched = true;
                                        tags1.remove(j);
                                        break;
                                    }
                                }

                                if (matched && !tagsZero) {
                                    JSONObject newObj = new JSONObject();
                                    newObj.put("Category", category);
                                    newObj.put("Count", count);
                                    newArray.put(newObj);
                                } else if (!matched) {
                                    JSONObject newObj = new JSONObject();
                                    newObj.put("Category", category);
                                    newObj.put("Count", count);
                                    newArray.put(newObj);
                                }

                            }
                            if ((tags1.size() != 0) && !first) {
                                for (int i = 0; i < tags1.size(); i++) {
                                    JSONObject newObj = new JSONObject();
                                    newObj.put("Category", tags1.get(i));
                                    newObj.put("Count", 1);
                                    newArray.put(newObj);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        WriteToFile(filename, newArray);
                    }
                });
            }
        }
    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    private ArrayList<LessonPlans> searchNoPreference(String query) {
        try {
            ArrayList<LessonPlans> uLessonPlans2 = uLessonPlans;
            ArrayList<LessonPlans> lessonPlans = new ArrayList<LessonPlans>();
            for (LessonPlans lessonPlan :
                    uLessonPlans) {
                if (lessonPlan.getName().toLowerCase().contains(query)) {
                    lessonPlans.add(lessonPlan);
                }
            }
            return lessonPlans;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HashMap<Integer, LessonPlans> search(String query) {
        Log.d("query", query);

        ArrayList<String> stringArrayList = new ArrayList<>();

        try {
            uLessonPlans = aTask.execute().get();


            jsonArray = ReadFromFile(filename);
            ArrayList<LessonPlans> lessonPlans = new ArrayList<LessonPlans>();
            for (LessonPlans lessonPlan :
                    uLessonPlans) {
                if (lessonPlan.getName().toLowerCase().contains(query)) {
                    lessonPlans.add(lessonPlan);
                    int totalCount = 0;
                    ArrayList<String> tags1 = lessonPlan.getTags();
                    for (String tag : tags1) {
                        if (hashMap.containsKey(tag)) {
                            totalCount = totalCount + hashMap.get(tag);
                        }
                    }
                    q.put(totalCount, lessonPlan);
                }
            }

            return q;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void WriteToFile(String filename, JSONArray jsonArray1) {
        FileOutputStream outputStream;
        try {
            outputStream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(jsonArray1.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONArray ReadFromFile(String filename) {
        FileInputStream inputStream;
        JSONArray jsonArray1 = null;
        JSONObject jsonObject1 = null;
        try {
            if (!fileExists(this, filename)) {
                FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write("[]".getBytes());
                outputStream.close();
            } else {
                inputStream = this.openFileInput(filename);
//                inputStream = (FileInputStream) this.getAssets().open(filename);
                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }
                    jsonArray1 = new JSONArray(stringBuilder.toString());
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        jsonObject1 = jsonArray1.getJSONObject(i);

                        hashMap.put(jsonObject1.getString("Category"), jsonObject1.getInt("Count"));
                    }
                    Log.d("json array", jsonArray1.toString());
                    inputStream.close();
                } else {
                    inputStream.close();
                    WriteToFile(filename, new JSONArray());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray1;
    }


}