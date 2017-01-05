package stem.cis3086.uom.stem;

import java.util.ArrayList;

/**
 * Created by elise on 05/01/2017.
 */

public class LessonPlans {
    private int Id ;
    private String Name ;
    private String ThumbnailUrl ;
    private String Summary ;
    private int MinGrade ;
    private int MaxGrade ;
    private String Subject ;
    private String Keywords ;
    private String ShortDescription ;
    private int Votes ;
    private String FileUrl ;
    private int RecommendedAge ;
    private String LessonFocus ;
    private String LessonSynopsis ;
    private String Objectives ;
    private String LearnerOutcomes ;
    private String LessonActivities ;
    private String Aligment ;
    private String RecommendeReading ;
    private String OptionalActivities ;
    private String LessonProcedure ;
    private String TimeNeeded ;
    private String InternetConnections ;
    private String ExtensionActivity ;
    private String SafetyNotes ;
    private String TeacherResources ;
    private String StudentNotes ;
    private String StudentWorksheets ;
    private ArrayList<String> tags;

    public ArrayList<String> getTags() {
        return tags;
    }

    public LessonPlans(){


    }

    public LessonPlans(String name, int votes){
        Name = name;
        Votes = votes;
    }

    public LessonPlans(int id, String name, String thumbnailUrl, String summary, int minGrade, int maxGrade, String subject, String keywords, String shortDescription, int votes, String fileUrl, int recommendedAge, String lessonFocus, String lessonSynopsis, String objectives, String learnerOutcomes, String lessonActivities, String aligment, String recommendeReading, String optionalActivities, String lessonProcedure, String timeNeeded, String internetConnections, String extensionActivity, String safetyNotes, String teacherResources, String studentNotes, String studentWorksheets, ArrayList<String> tagHelpers) {
        Id = id;
        Name = name;
        ThumbnailUrl = thumbnailUrl;
        Summary = summary;
        MinGrade = minGrade;
        MaxGrade = maxGrade;
        Subject = subject;
        Keywords = keywords;
        ShortDescription = shortDescription;
        Votes = votes;
        FileUrl = fileUrl;
        RecommendedAge = recommendedAge;
        LessonFocus = lessonFocus;
        LessonSynopsis = lessonSynopsis;
        Objectives = objectives;
        LearnerOutcomes = learnerOutcomes;
        LessonActivities = lessonActivities;
        Aligment = aligment;
        RecommendeReading = recommendeReading;
        OptionalActivities = optionalActivities;
        LessonProcedure = lessonProcedure;
        TimeNeeded = timeNeeded;
        InternetConnections = internetConnections;
        ExtensionActivity = extensionActivity;
        SafetyNotes = safetyNotes;
        TeacherResources = teacherResources;
        StudentNotes = studentNotes;
        StudentWorksheets = studentWorksheets;
        tags = tagHelpers;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getThumbnailUrl() {
        return ThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        ThumbnailUrl = thumbnailUrl;
    }

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public int getMinGrade() {
        return MinGrade;
    }

    public void setMinGrade(int minGrade) {
        MinGrade = minGrade;
    }

    public int getMaxGrade() {
        return MaxGrade;
    }

    public void setMaxGrade(int maxGrade) {
        MaxGrade = maxGrade;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getKeywords() {
        return Keywords;
    }

    public void setKeywords(String keywords) {
        Keywords = keywords;
    }

    public String getShortDescription() {
        return ShortDescription;
    }

    public void setShortDescription(String shortDescription) {
        ShortDescription = shortDescription;
    }

    public int getVotes() {
        return Votes;
    }

    public void setVotes(int votes) {
        Votes = votes;
    }

    public String getFileUrl() {
        return FileUrl;
    }

    public void setFileUrl(String fileUrl) {
        FileUrl = fileUrl;
    }

    public int getRecommendedAge() {
        return RecommendedAge;
    }

    public void setRecommendedAge(int recommendedAge) {
        RecommendedAge = recommendedAge;
    }

    public String getLessonFocus() {
        return LessonFocus;
    }

    public void setLessonFocus(String lessonFocus) {
        LessonFocus = lessonFocus;
    }

    public String getLessonSynopsis() {
        return LessonSynopsis;
    }

    public void setLessonSynopsis(String lessonSynopsis) {
        LessonSynopsis = lessonSynopsis;
    }

    public String getObjectives() {
        return Objectives;
    }

    public void setObjectives(String objectives) {
        Objectives = objectives;
    }

    public String getLearnerOutcomes() {
        return LearnerOutcomes;
    }

    public void setLearnerOutcomes(String learnerOutcomes) {
        LearnerOutcomes = learnerOutcomes;
    }

    public String getLessonActivities() {
        return LessonActivities;
    }

    public void setLessonActivities(String lessonActivities) {
        LessonActivities = lessonActivities;
    }

    public String getAligment() {
        return Aligment;
    }

    public void setAligment(String aligment) {
        Aligment = aligment;
    }

    public String getRecommendeReading() {
        return RecommendeReading;
    }

    public void setRecommendeReading(String recommendeReading) {
        RecommendeReading = recommendeReading;
    }

    public String getOptionalActivities() {
        return OptionalActivities;
    }

    public void setOptionalActivities(String optionalActivities) {
        OptionalActivities = optionalActivities;
    }

    public String getLessonProcedure() {
        return LessonProcedure;
    }

    public void setLessonProcedure(String lessonProcedure) {
        LessonProcedure = lessonProcedure;
    }

    public String getTimeNeeded() {
        return TimeNeeded;
    }

    public void setTimeNeeded(String timeNeeded) {
        TimeNeeded = timeNeeded;
    }

    public String getInternetConnections() {
        return InternetConnections;
    }

    public void setInternetConnections(String internetConnections) {
        InternetConnections = internetConnections;
    }

    public String getExtensionActivity() {
        return ExtensionActivity;
    }

    public void setExtensionActivity(String extensionActivity) {
        ExtensionActivity = extensionActivity;
    }

    public String getSafetyNotes() {
        return SafetyNotes;
    }

    public void setSafetyNotes(String safetyNotes) {
        SafetyNotes = safetyNotes;
    }

    public String getTeacherResources() {
        return TeacherResources;
    }

    public void setTeacherResources(String teacherResources) {
        TeacherResources = teacherResources;
    }

    public String getStudentNotes() {
        return StudentNotes;
    }

    public void setStudentNotes(String studentNotes) {
        StudentNotes = studentNotes;
    }

    public String getStudentWorksheets() {
        return StudentWorksheets;
    }

    public void setStudentWorksheets(String studentWorksheets) {
        StudentWorksheets = studentWorksheets;
    }



}
