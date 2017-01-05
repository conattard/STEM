package stem.cis3086.uom.stem.fragments;


import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.pnikosis.materialishprogress.ProgressWheel;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import stem.cis3086.uom.stem.LessonDetailActivity;
import stem.cis3086.uom.stem.R;

public class LessonPlanFragment extends Fragment {

    private static final String ARG_ID = "argId";
    private String lessonId;

    private ProgressWheel progressWheel;
    private ScrollView scrollView;
    private WebView shortDescriptionWebView;
    private WebView focusWebView;
    private WebView synopsisWebView;
    private WebView objectivesWebView;
    private WebView learningOutcomesWebView;
    private WebView activitiesWebView;
    private WebView alignmentWebView;
    private WebView recommendedReadingWebView;
    private WebView optionalActivitiesWebView;
    private WebView teacherResourcesWebView;
    private WebView studentNotesWebView;
    private ImageButton shortDescriptionCollapseButton;
    private ImageButton focusCollapseButton;
    private ImageButton synopsisCollapseButton;
    private ImageButton objectivesCollapseButton;
    private ImageButton learningOutcomesButton;
    private ImageButton activitiesCollapseButton;
    private ImageButton alignmentCollapseButton;
    private ImageButton recommendedReadingCollapseButton;
    private ImageButton optionalActivitiesCollapseButton;
    private ImageButton teacherResourcesCollapseButton;
    private ImageButton studentNotesCollapseButton;

    public LessonPlanFragment() {
        // Required empty public constructor
    }

    public static LessonPlanFragment newInstance(String lessonId) {
        LessonPlanFragment fragment = new LessonPlanFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_lesson_plan, container, false);

        findViews(rootView);
        getData();

        return rootView;
    }

    private void findViews(View view){
        progressWheel = (ProgressWheel) view.findViewById(R.id.lessonPlanProgressWheel);
        scrollView = (ScrollView) view.findViewById(R.id.lessonPlanScrollView);
        shortDescriptionWebView = (WebView) view.findViewById(R.id.lessonPlanShortDescriptionWebView);
        focusWebView = (WebView) view.findViewById(R.id.lessonPlanFocusWebView);
        synopsisWebView = (WebView) view.findViewById(R.id.lessonPlanSynopsisWebView);
        objectivesWebView = (WebView) view.findViewById(R.id.lessonPlanObjectivesWebView);
        learningOutcomesWebView = (WebView) view.findViewById(R.id.lessonPlanLearningOutcomessWebView);
        activitiesWebView = (WebView) view.findViewById(R.id.lessonPlanActivitiesWebView);
        alignmentWebView = (WebView) view.findViewById(R.id.lessonPlanAlignmentWebView);
        recommendedReadingWebView = (WebView) view.findViewById(R.id.lessonPlanRecommendedReadingWebView);
        optionalActivitiesWebView = (WebView) view.findViewById(R.id.lessonPlanOptionalActivitiesWebView);
        teacherResourcesWebView = (WebView) view.findViewById(R.id.lessonPlanTeacherResourcesWebView);
        studentNotesWebView = (WebView) view.findViewById(R.id.lessonPlanStudentNotesWebView);

        shortDescriptionCollapseButton = (ImageButton) view.findViewById(R.id.lessonPlanShortDescriptionButton);
        focusCollapseButton = (ImageButton) view.findViewById(R.id.lessonPlanFocusButton);
        synopsisCollapseButton = (ImageButton) view.findViewById(R.id.lessonPlanSynopsisButton);
        objectivesCollapseButton = (ImageButton) view.findViewById(R.id.lessonPlanObjectivesButton);
        learningOutcomesButton = (ImageButton) view.findViewById(R.id.lessonPlanLearningOutcomesButton);
        activitiesCollapseButton = (ImageButton) view.findViewById(R.id.lessonPlanActivitiesButton);
        alignmentCollapseButton = (ImageButton) view.findViewById(R.id.lessonPlanAlignmentButton);
        recommendedReadingCollapseButton = (ImageButton) view.findViewById(R.id.lessonPlanRecommendedReadingButton);
        optionalActivitiesCollapseButton = (ImageButton) view.findViewById(R.id.lessonPlanOptionalActivitiesButton);
        teacherResourcesCollapseButton = (ImageButton) view.findViewById(R.id.lessonPlanTeacherResourcesButton);
        studentNotesCollapseButton = (ImageButton) view.findViewById(R.id.lessonPlanStudentNotesButton);
    }

    private void getData(){
        // Show progress wheel
        progressWheel.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        // Get data
        String path = LessonDetailActivity.LESSON_PATH + lessonId;
        Ion.with(getActivity())
                .load(path)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        // Get lesson plan from array
                        JsonObject lessonPlan = result.get(0).getAsJsonObject();

                        // Get data from lesson plan
                        String shortDescriptionHtml = lessonPlan.get("ShortDescription").getAsString();
                        setupWebView(shortDescriptionWebView, shortDescriptionCollapseButton, shortDescriptionHtml);

                        String lessonFocusHtml = lessonPlan.get("LessonFocus").getAsString();
                        setupWebView(focusWebView, focusCollapseButton, lessonFocusHtml);

                        String lessonSynopsisHtml = lessonPlan.get("LessonSynopsis").getAsString();
                        setupWebView(synopsisWebView, synopsisCollapseButton, lessonSynopsisHtml);

                        String objectives = lessonPlan.get("Objectives").getAsString();
                        setupWebView(objectivesWebView, objectivesCollapseButton, objectives);

                        String learningOutcomesHtml = lessonPlan.get("LearnerOutcomes").getAsString();
                        setupWebView(learningOutcomesWebView, learningOutcomesButton, learningOutcomesHtml);

                        String activitiesHtml = lessonPlan.get("LessonActivities").getAsString();
                        setupWebView(activitiesWebView, activitiesCollapseButton, activitiesHtml);

                        String aligmentHtml = lessonPlan.get("Aligment").getAsString();
                        setupWebView(alignmentWebView, alignmentCollapseButton, aligmentHtml);

                        String recommendedReadingHtml = lessonPlan.get("RecommendeReading").getAsString();
                        setupWebView(recommendedReadingWebView, recommendedReadingCollapseButton, recommendedReadingHtml);

                        String optionalActivitiesHtml = lessonPlan.get("OptionalActivities").getAsString();
                        setupWebView(optionalActivitiesWebView, optionalActivitiesCollapseButton, optionalActivitiesHtml);

                        String teacherResourcesHtml = lessonPlan.get("TeacherResources").getAsString();
                        setupWebView(teacherResourcesWebView, teacherResourcesCollapseButton, teacherResourcesHtml);

                        String studentNotesHtml = lessonPlan.get("StudentNotes").getAsString();
                        setupWebView(studentNotesWebView, studentNotesCollapseButton, studentNotesHtml);

                        // Hide progress wheel
                        progressWheel.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void setupWebView(final WebView webView, final ImageButton collapseButton, final String htmlContext){
        // Set html content
        webView.loadData(htmlContext, "text/html; charset=utf-8", "UTF-8");

        // Set background color
        webView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background));

        // Setup collapse button
        collapseButton.setImageResource(webView.getVisibility() == View.VISIBLE ? R.drawable.ic_chevron_up_black_24dp : R.drawable.ic_chevron_down_black_24dp);
        collapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.setVisibility(webView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                collapseButton.setImageResource(webView.getVisibility() == View.VISIBLE ? R.drawable.ic_chevron_up_black_24dp : R.drawable.ic_chevron_down_black_24dp);
            }
        });
    }
}
