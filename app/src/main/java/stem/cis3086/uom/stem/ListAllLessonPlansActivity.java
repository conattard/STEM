package stem.cis3086.uom.stem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ListAllLessonPlansActivity extends AppCompatActivity {

    ListView listView;
    HelperClass helperClass = new HelperClass();
    ArrayList<LessonPlans> lessonPlansArrayList = helperClass.getAllLessonPlans();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_lesson_plans);
        listView = (ListView)findViewById(R.id.allListView);
        listView.setAdapter(new LessonPlansAdapter(this, R.layout.search_item_layout, lessonPlansArrayList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch post detail
                LessonPlans clicked = ((LessonPlans) listView.getItemAtPosition(position));
                String lessonId = String.valueOf(clicked.getId());
                Intent intent = new Intent(ListAllLessonPlansActivity.this, LessonDetailActivity.class);
                intent.putExtra(LessonDetailActivity.EXTRA_LESSON_ID, lessonId);
                startActivity(intent);
            }
        });
    }
}
