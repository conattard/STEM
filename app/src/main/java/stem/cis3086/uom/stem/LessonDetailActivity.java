package stem.cis3086.uom.stem;

import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import stem.cis3086.uom.stem.fragments.LessonPlanFragment;
import stem.cis3086.uom.stem.fragments.ResourcesFragment;
import stem.cis3086.uom.stem.fragments.ScienceFragment;

public class LessonDetailActivity extends AppCompatActivity {

    public static final String LESSON_PATH = "http://stemapp.azurewebsites.net/LessonPlans/GetLessonPlansById?id=";
    public final String EXTRA_LESSON_ID = "extraLessonId";
    private String lessonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);

        // Get lesson id from extra
        if (getIntent().hasExtra(EXTRA_LESSON_ID)){
            lessonId = getIntent().getStringExtra(EXTRA_LESSON_ID);
        } else  {
            // TODO: Remove from release
            lessonId = "15";
        }

        // Setup view pager
        ViewPager viewPager = (ViewPager) findViewById(R.id.lessonDetailViewPager);
        viewPager.setAdapter(new LessonPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.lessonDetailTabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private class LessonPagerAdapter extends FragmentPagerAdapter{

        private String[] names = {"Lesson", "Resources", "Science"};
        private Fragment[] fragments = {
                LessonPlanFragment.newInstance(lessonId),
                ResourcesFragment.newInstance(lessonId),
                ScienceFragment.newInstance(lessonId),
        };

        public LessonPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return names[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }
}
