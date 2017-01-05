package stem.cis3086.uom.stem;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView topLPRecyclerView, SLPRecyclerView, TLPRecyclerView, ELPRecyclerView, MLPRecyclerView;
    private RecyclerViewImagesAdapter recyclerViewImagesAdapter;
    private DrawerLayout mDrawerLayout;
    private ListView mListView;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<String> optionsArray;
    private ArrayList<LessonPlans> lessonPlansArrayList;
    private HelperClass helperClass = new HelperClass();
    private String[] imageUrls = {
            "http://www.w3schools.com/css/trolltunga.jpg",
            "https://cdn.pixabay.com/photo/2016/03/28/12/35/cat-1285634_960_720.png",
            "http://wallpaper-gallery.net/images/images/images-17.jpg",
            "http://www.freedigitalphotos.net/images/img/homepage/87357.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //prevents network on main thread exception
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);


        }

        lessonPlansArrayList = helperClass.getAllLessonPlans();

        setUpRecyclerViews();
        setUpDrawer();


        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }



    public ArrayList<LessonPlans> getLatest5LessonPlans(){

        ArrayList<LessonPlans> latest5 = new ArrayList<>();
        if(lessonPlansArrayList != null){
            for(int i = 1; i <= 5; i ++) {
                latest5.add(lessonPlansArrayList.get(lessonPlansArrayList.size() - i));
            }
        }
        return latest5;
    }

    private ArrayList<LessonPlans> getTop5RatedLessonPlans(){
        ArrayList<LessonPlans> lessonPlansSorted = lessonPlansArrayList; //to be sorted in ascending order according to votes

        Collections.sort(lessonPlansSorted, new Comparator<LessonPlans>() {
            @Override
            public int compare(LessonPlans o1, LessonPlans o2) {
                return o2.getVotes()  - o1.getVotes();
            }
        });
        ArrayList<LessonPlans> list = new ArrayList<>();
        for(int i = 0 ; i < 5 ; i++){
            list.add(lessonPlansSorted.get(i));
        }
        return list;
    }

    private ArrayList<LessonPlans> getNonDuplicatesList(){
        ArrayList<LessonPlans> nonDups = new ArrayList<>();
        ArrayList<LessonPlans> fullList = getTop5RatedLessonPlans();
        ArrayList<LessonPlans> latest = getLatest5LessonPlans();
        fullList.addAll(latest);
        ArrayList<LessonPlans> lessonPlansArrayList2 = fullList;
        for(LessonPlans lessonPlan : lessonPlansArrayList){
            boolean matched = false;
            boolean secondMatched = false;

            for(LessonPlans lessonPlan2 : lessonPlansArrayList2){
                if(lessonPlan.getName().equals(lessonPlan2.getName()) && lessonPlan.getVotes() == lessonPlan2.getVotes()){
                    matched = true;
                    break;
                }

            }
            if(!matched){
                nonDups.add(lessonPlan);
            }
        }

        for(LessonPlans lessonPlan : lessonPlansArrayList2){
            boolean matched = false;

            for(LessonPlans lessonPlan2 : lessonPlansArrayList){
                if(lessonPlan.getName().equals(lessonPlan2.getName()) && lessonPlan.getVotes() == lessonPlan2.getVotes()){
                    matched = true;
                    break;
                }

            }
            if(!matched){
                nonDups.add(lessonPlan);
            }
        }

        return  nonDups;
    }


    private ArrayList<LessonPlans> getRandom5LessonPlans(){

        Random r = new Random();
        ArrayList<LessonPlans> randomPlans = new ArrayList<>();
        //ArrayList<LessonPlans> nonDupsCopy = getNonDuplicatesList() ;
        ArrayList<Integer> integerArrayList = new ArrayList<>();

        while(randomPlans.size() != 5) {
            boolean matched = false;
            int randInt = Math.abs(r.nextInt()) % lessonPlansArrayList.size();
            for(int integer : integerArrayList){
                if(randInt == integer){
                    matched = true;
                    break;
                }
            }
            if(!matched) {
                randomPlans.add(lessonPlansArrayList.get(randInt));
                integerArrayList.add(randInt);
            }

        }
    /*
    if(nonDupsCopy.size() < 5){
    for(int i = 0; i < nonDupsCopy.size(); i++) {
        int randInt = Math.abs(r.nextInt()) % nonDupsCopy.size();
        randomPlans.add(nonDupsCopy.get(randInt));
        nonDupsCopy.remove(randInt);
    }
    }else{
        for(int i = 0; i < 5; i++) {
            int randInt = Math.abs(r.nextInt()) % nonDupsCopy.size();
            randomPlans.add(nonDupsCopy.get(randInt));
            nonDupsCopy.remove(randInt);
        }
    }
    */

        return randomPlans;
    }


    public void setUpDrawer(){
        try {
            optionsArray = new ArrayList<String>();
            //optionsArray.add("Account");
            optionsArray.add("View all lesson plans");
            //optionsArray.add("options");
            //optionsArray.add("Settings");

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mListView = (ListView) findViewById(R.id.left_drawer);

            mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, optionsArray));

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            Intent intent = new Intent(DashboardActivity.this, ListAllLessonPlansActivity.class);
                            startActivity(intent);
                            break;
                    }
                }
            });



            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close){
                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    getSupportActionBar().setTitle("Drawer");
                    invalidateOptionsMenu();
                }

                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    getSupportActionBar().setTitle(getTitle().toString());
                }
            };

            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.setDrawerListener(mDrawerToggle);



        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "An error has occured, drawer might not be set up properly", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpRecyclerViews(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);


        ArrayList<LessonPlans> toprated = getTop5RatedLessonPlans();
        ArrayList<LessonPlans>latest = getLatest5LessonPlans();
        ArrayList<LessonPlans>random = getRandom5LessonPlans();
        ArrayList<LessonPlans>random2 = getRandom5LessonPlans();
        ArrayList<LessonPlans>random3 = getRandom5LessonPlans();
        //ArrayList<LessonPlans>recommended = getRecommendedLp();


        LessonPlans lessonPlans = new LessonPlans();
        lessonPlans.setThumbnailUrl(imageUrls[0]);

        LessonPlans lessonPlans2 = new LessonPlans();
        lessonPlans2.setThumbnailUrl(imageUrls[1]);

        LessonPlans lessonPlans3 = new LessonPlans();
        lessonPlans3.setThumbnailUrl(imageUrls[2]);


        LessonPlans lessonPlans4 = new LessonPlans();
        lessonPlans4.setThumbnailUrl(imageUrls[3]);

        ArrayList<LessonPlans>lessonPlansArraysList = new ArrayList<>();
        lessonPlansArraysList.add(lessonPlans);
        lessonPlansArraysList.add(lessonPlans2);
        lessonPlansArraysList.add(lessonPlans3);
        lessonPlansArraysList.add(lessonPlans4);




        //recyclerViewImagesAdapter = new RecyclerViewImagesAdapter(this,toprated);
        topLPRecyclerView = (RecyclerView)findViewById(R.id.top_recycler_view);
        topLPRecyclerView.setAdapter(new RecyclerViewImagesAdapter(this, toprated));
        topLPRecyclerView.setLayoutManager(layoutManager);


        //recyclerViewImagesAdapter = new RecyclerViewImagesAdapter(this, lessonPlansArrayList);
        SLPRecyclerView = (RecyclerView)findViewById(R.id.science_recycler_view);
        SLPRecyclerView.setAdapter(new RecyclerViewImagesAdapter(this, random));
        SLPRecyclerView.setLayoutManager(layoutManager1);


        //recyclerViewImagesAdapter = new RecyclerViewImagesAdapter(this, lessonPlansArrayList);
        TLPRecyclerView = (RecyclerView)findViewById(R.id.technology_recycler_view);
        TLPRecyclerView.setAdapter(new RecyclerViewImagesAdapter(this, latest));
        TLPRecyclerView.setLayoutManager(layoutManager2);


        //recyclerViewImagesAdapter = new RecyclerViewImagesAdapter(this,lessonPlansArrayList);
        ELPRecyclerView = (RecyclerView)findViewById(R.id.engineering_recycler_view);
        ELPRecyclerView.setAdapter(new RecyclerViewImagesAdapter(this, random2));
        ELPRecyclerView.setLayoutManager(layoutManager3);


        MLPRecyclerView = (RecyclerView)findViewById(R.id.math_recycler_view);
        MLPRecyclerView.setAdapter(new RecyclerViewImagesAdapter(this, random3));
        MLPRecyclerView.setLayoutManager(layoutManager4);



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling, menu);
        try {

            // Get the SearchView and set the searchable configuration
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            // Assumes current activity is the searchable activity
            searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
            searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default



            return true;
        }catch(Exception e){
            Toast.makeText(this, "Not all (or none of the) menu bar options could be loaded", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return false;
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
