package com.peeplotech.studygroup.databases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import com.peeplotech.studygroup.models.Analytics;
import com.peeplotech.studygroup.models.Assessment;
import com.peeplotech.studygroup.models.AssessmentResult;
import com.peeplotech.studygroup.models.Assignment;
import com.peeplotech.studygroup.models.Chat;
import com.peeplotech.studygroup.models.Course;
import com.peeplotech.studygroup.models.Module;
import com.peeplotech.studygroup.models.SubmittedAssignment;
import com.peeplotech.studygroup.models.SubscribedCourse;
import com.peeplotech.studygroup.models.User;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "generaldb.db";
    private static final int DB_VER = 1;
    public static final String USER_TABLE = "Users";
    public static final String COURSE_TABLE = "Courses";
    public static final String SUBSCRIBED_COURSE_TABLE = "SubscribedCourses";
    public static final String MODULE_TABLE = "Modules";
    public static final String ASSIGNMENT_TABLE = "Assignments";
    public static final String ASSESSMENT_TABLE = "Assessment";
    public static final String ASSESSMENT_RESULT_TABLE = "AssessmentResult";
    public static final String ANALYTICS_TABLE = "Analytics";

    //init database system
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }







    /*---   USER   ---*/
    //check if user exist
    public boolean userExists(String userIdentity){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + USER_TABLE + " WHERE user_id = '%s';", userIdentity);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    //register new user
    public void registerNewUser(String userIdentity, String firstName, String lastName, String password, String userType, String userAvatar){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO " + USER_TABLE + " (user_id, first_name, last_name, password, user_type, user_avatar) VALUES('%s', '%s', '%s', '%s', '%s', '%s');",
                userIdentity,
                firstName,
                lastName,
                password,
                userType,
                userAvatar);
        db.execSQL(query);
    }

    public void updateUserDyslexicDetail(String userIdentity, float dyslexicScore ){
        SQLiteDatabase db = getReadableDatabase();
        Boolean isDyslexic = false;
        if(dyslexicScore < 2){
            isDyslexic = true;
        }
        String query = String.format("UPDATE " + USER_TABLE+ " SET dyslexic_score = '%f', is_dyslexic='%b' WHERE user_id = '%s';",
                dyslexicScore,isDyslexic,userIdentity
                );
        db.execSQL(query);
    }

    //login user
    public boolean loginUser(String userIdentity, String password){
        boolean isSuccess = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + USER_TABLE + " WHERE user_id = '%s' AND password = '%s';", userIdentity, password);

        //check if correct
        cursor = db.rawQuery(SQLQuery, null);
        isSuccess = cursor.getCount() > 0;
        cursor.close();

        return isSuccess;
    }

    //get user data
    public User getUserDetails(String userIdentity){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + USER_TABLE + " WHERE user_id = '%s';", userIdentity);

        //make null user
        User currentUser = null;

        //run query
        cursor = db.rawQuery(SQLQuery, null);

        //check again if data exists
        if (cursor.getCount() > 0){

            cursor.moveToFirst();
            currentUser = new User(
                    cursor.getString(cursor.getColumnIndex("user_id")),
                    cursor.getString(cursor.getColumnIndex("first_name")),
                    cursor.getString(cursor.getColumnIndex("last_name")),
                    cursor.getString(cursor.getColumnIndex("password")),
                    cursor.getString(cursor.getColumnIndex("user_type")),
                    cursor.getString(cursor.getColumnIndex("user_avatar"))
            );

        }
        cursor.close();

        return currentUser;
    }

    //update user profile
    public void updateUserProfile(String userIdentity, String firstName, String lastName, String userAvatar){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE " + USER_TABLE + " SET first_name = '%s', last_name = '%s', user_avatar = '%s'  WHERE user_id = '%s';",
                firstName,
                lastName,
                userAvatar,
                userIdentity);
        db.execSQL(query);
    }

    //get all users
    public List<User> getAllUsers() {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + USER_TABLE + ";");
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<User> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new User(
                            cursor.getString(cursor.getColumnIndex("user_id")),
                            cursor.getString(cursor.getColumnIndex("first_name")),
                            cursor.getString(cursor.getColumnIndex("last_name")),
                            cursor.getString(cursor.getColumnIndex("password")),
                            cursor.getString(cursor.getColumnIndex("user_type")),
                            cursor.getString(cursor.getColumnIndex("user_avatar"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //delete user
    public void deleteUser(String userIdentity){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM " + USER_TABLE + " WHERE user_id = '%s';", userIdentity);
        db.execSQL(query);
    }









    /*---   COURSE   ---*/
    //check if course id is already in use
    public boolean isCourseIdInUse(String courseId){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + COURSE_TABLE + " WHERE course_id = '%s';", courseId);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    //create course
    public void createNewCourse(String courseId, String courseOwner, String courseTitle, String courseSubTitle, String courseBrief, String courseImg){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO " + COURSE_TABLE + " (course_id, course_owner, course_title, course_subtitle, course_brief, course_img) VALUES('%s', '%s', '%s', '%s', '%s', '%s');",
                courseId,
                courseOwner,
                courseTitle,
                courseSubTitle,
                courseBrief,
                courseImg);
        db.execSQL(query);
    }

    //get course details
    public Course getCourseDetails(String courseId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + COURSE_TABLE + " WHERE course_id = '%s';", courseId);

        //make null user
        Course currentCourse = null;

        //run query
        cursor = db.rawQuery(SQLQuery, null);

        //check again if data exists
        if (cursor.getCount() > 0){

            cursor.moveToFirst();
            currentCourse = new Course(
                    cursor.getString(cursor.getColumnIndex("course_id")),
                    cursor.getString(cursor.getColumnIndex("course_owner")),
                    cursor.getString(cursor.getColumnIndex("course_title")),
                    cursor.getString(cursor.getColumnIndex("course_subtitle")),
                    cursor.getString(cursor.getColumnIndex("course_brief")),
                    cursor.getString(cursor.getColumnIndex("course_img"))
            );

        }
        cursor.close();

        return currentCourse;
    }

    //delete course
    public void deleteCourse(String courseId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM " + COURSE_TABLE + " WHERE course_id = '%s';", courseId);
        db.execSQL(query);
    }

    //update course details
    public void updateCourseDetails(String courseId, String courseTitle, String courseSubTitle, String courseBrief, String courseImg){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE " + COURSE_TABLE + " SET course_title = '%s', course_subtitle = '%s', course_brief = '%s', course_img = '%s'  WHERE course_id = '%s';",
                courseTitle,
                courseSubTitle,
                courseBrief,
                courseImg,
                courseId);
        db.execSQL(query);
    }

    //get all courses
    public List<Course> getAllCourses() {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + COURSE_TABLE + ";");
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<Course> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new Course(
                            cursor.getString(cursor.getColumnIndex("course_id")),
                            cursor.getString(cursor.getColumnIndex("course_owner")),
                            cursor.getString(cursor.getColumnIndex("course_title")),
                            cursor.getString(cursor.getColumnIndex("course_subtitle")),
                            cursor.getString(cursor.getColumnIndex("course_brief")),
                            cursor.getString(cursor.getColumnIndex("course_img"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //get all courses created by lecturer [id]
    public List<Course> getAllCreatedCourses(String userIdentity) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + COURSE_TABLE + " WHERE course_owner = '%s';", userIdentity);
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<Course> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new Course(
                            cursor.getString(cursor.getColumnIndex("course_id")),
                            cursor.getString(cursor.getColumnIndex("course_owner")),
                            cursor.getString(cursor.getColumnIndex("course_title")),
                            cursor.getString(cursor.getColumnIndex("course_subtitle")),
                            cursor.getString(cursor.getColumnIndex("course_brief")),
                            cursor.getString(cursor.getColumnIndex("course_img"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //get all courses student subscribed to [id]
    public List<SubscribedCourse> getAllSubscribedCourses(String userIdentity) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + SUBSCRIBED_COURSE_TABLE + " WHERE student_id = '%s';", userIdentity);
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<SubscribedCourse> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new SubscribedCourse(
                            cursor.getString(cursor.getColumnIndex("student_id")),
                            cursor.getString(cursor.getColumnIndex("course_id"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //get all student subscribed to my course
    public List<SubscribedCourse> getAllStudentsSubscribed(String courseId) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + SUBSCRIBED_COURSE_TABLE + " WHERE course_id = '%s';", courseId);
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<SubscribedCourse> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new SubscribedCourse(
                            cursor.getString(cursor.getColumnIndex("student_id")),
                            cursor.getString(cursor.getColumnIndex("course_id"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //search course
    public List<Course> getQueriedCourses(String searchQuery) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        //String SQLQuery = String.format(, searchQuery);
        String SQLQuery = "SELECT * FROM " + COURSE_TABLE + " WHERE course_title LIKE '%" + searchQuery + "%';";
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<Course> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new Course(
                            cursor.getString(cursor.getColumnIndex("course_id")),
                            cursor.getString(cursor.getColumnIndex("course_owner")),
                            cursor.getString(cursor.getColumnIndex("course_title")),
                            cursor.getString(cursor.getColumnIndex("course_subtitle")),
                            cursor.getString(cursor.getColumnIndex("course_brief")),
                            cursor.getString(cursor.getColumnIndex("course_img"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //check if i have subscribed to course
    public boolean haveISubscribed(String userId, String courseId){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + SUBSCRIBED_COURSE_TABLE + " WHERE student_id = '%s' AND course_id = '%s';", userId, courseId);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    //subscribe to course
    public void subscribeToCourse(String userId, String courseId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO " + SUBSCRIBED_COURSE_TABLE + " (student_id, course_id) VALUES('%s', '%s');",
                userId,
                courseId);
        db.execSQL(query);
    }

    //unsubscribe to course
    public void unsubscribeToCourse(String userId, String courseId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM " + SUBSCRIBED_COURSE_TABLE + " WHERE student_id = '%s' AND course_id = '%s';", userId, courseId);
        db.execSQL(query);
    }










    /*---   modules   ---*/
    //check if module id is already in use
    public boolean isModuleIdInUse(String moduleId){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + MODULE_TABLE + " WHERE module_id = '%s';", moduleId);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    //create assignment
    public void createNewModule(String moduleId, String courseId, String moduleTitle, String moduleDesc, String moduleType, String moduleFile){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO " + MODULE_TABLE + " (module_id, course_id, module_title, module_desc, module_type, module_file) VALUES('%s', '%s', '%s', '%s', '%s', '%s');",
                moduleId,
                courseId,
                moduleTitle,
                moduleDesc,
                moduleType,
                moduleFile);
        db.execSQL(query);
    }

    //get all modules
    public List<Module> getAllModules(String courseId) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + MODULE_TABLE + " WHERE course_id = '%s';", courseId);
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<Module> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new Module(
                            cursor.getString(cursor.getColumnIndex("module_id")),
                            cursor.getString(cursor.getColumnIndex("course_id")),
                            cursor.getString(cursor.getColumnIndex("module_title")),
                            cursor.getString(cursor.getColumnIndex("module_desc")),
                            cursor.getString(cursor.getColumnIndex("module_type")),
                            cursor.getString(cursor.getColumnIndex("module_file"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //get module details
    public Module getModuleDetails(String moduleId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + MODULE_TABLE + " WHERE module_id = '%s';", moduleId);

        //make null user
        Module currentModule = null;

        //run query
        cursor = db.rawQuery(SQLQuery, null);

        //check again if data exists
        if (cursor.getCount() > 0){

            cursor.moveToFirst();
            currentModule = new Module(
                    cursor.getString(cursor.getColumnIndex("module_id")),
                    cursor.getString(cursor.getColumnIndex("course_id")),
                    cursor.getString(cursor.getColumnIndex("module_title")),
                    cursor.getString(cursor.getColumnIndex("module_desc")),
                    cursor.getString(cursor.getColumnIndex("module_type")),
                    cursor.getString(cursor.getColumnIndex("module_file"))
            );

        }
        cursor.close();

        return currentModule;
    }

    //delete module
    public void deleteModule(String moduleId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM " + MODULE_TABLE + " WHERE module_id = '%s';", moduleId);
        db.execSQL(query);
    }









    /*---   assignments   ---*/
    //check if module id is already in use
    public boolean isAssignmentIdInUse(String assignmentId){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + ASSIGNMENT_TABLE + " WHERE assignment_id = '%s';", assignmentId);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    //create assignment
    public void createNewAssignment(String assignmentId, String assignmentTitle, String score, String deadline, String type, String file, String courseId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO " + ASSIGNMENT_TABLE + " (assignment_id, assignment_title, assignment_score, assignment_deadline, assignment_type, assignment_file, course_id)" +
                        " VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                assignmentId,
                assignmentTitle,
                score,
                deadline,
                type,
                file,
                courseId);
        db.execSQL(query);
    }

    //get all assignments
    public List<Assignment> getAllAssignments(String courseId) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + ASSIGNMENT_TABLE + " WHERE course_id = '%s';", courseId);
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<Assignment> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new Assignment(
                            cursor.getString(cursor.getColumnIndex("assignment_id")),
                            cursor.getString(cursor.getColumnIndex("assignment_title")),
                            cursor.getString(cursor.getColumnIndex("assignment_score")),
                            cursor.getString(cursor.getColumnIndex("assignment_deadline")),
                            cursor.getString(cursor.getColumnIndex("assignment_type")),
                            cursor.getString(cursor.getColumnIndex("assignment_file")),
                            cursor.getString(cursor.getColumnIndex("course_id"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //get assignment details
    public Assignment getAssignmentDetails(String assignmentId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + ASSIGNMENT_TABLE + " WHERE assignment_id = '%s';", assignmentId);

        //make null user
        Assignment currentAssignment = null;

        //run query
        cursor = db.rawQuery(SQLQuery, null);

        //check again if data exists
        if (cursor.getCount() > 0){

            cursor.moveToFirst();
            currentAssignment = new Assignment(
                    cursor.getString(cursor.getColumnIndex("assignment_id")),
                    cursor.getString(cursor.getColumnIndex("assignment_title")),
                    cursor.getString(cursor.getColumnIndex("assignment_score")),
                    cursor.getString(cursor.getColumnIndex("assignment_deadline")),
                    cursor.getString(cursor.getColumnIndex("assignment_type")),
                    cursor.getString(cursor.getColumnIndex("assignment_file")),
                    cursor.getString(cursor.getColumnIndex("course_id"))
            );

        }
        cursor.close();

        return currentAssignment;
    }

    //delete assignment
    public void deleteAssignment(String assignmentId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM " + ASSIGNMENT_TABLE + " WHERE assignment_id = '%s';", assignmentId);
        db.execSQL(query);
    }

    //create folder table
    public void createAssignmentSubmissionTable(String tableId){
        //init db
        SQLiteDatabase db = getWritableDatabase();

        //create folder query
        db.execSQL(" CREATE TABLE IF NOT EXISTS " + tableId + " (" +
                "\"id\"" + " INTEGER NOT NULL UNIQUE," +
                "\"submission_id\"" + " TEXT NOT NULL, " +
                "\"student_id\"" + " TEXT NOT NULL, " +
                "\"assignment_score\"" + " TEXT NOT NULL, " +
                "\"course_id\"" + " TEXT NOT NULL, " +
                "\"assignment_file\"" + " TEXT NOT NULL, " +
                "PRIMARY KEY(" + "\"id\" AUTOINCREMENT));");
    }

    //check if submitted id is already in use
    public boolean isSubmittedAssignmentIdInUse(String tableId, String submissionId){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + tableId + " WHERE submission_id = '%s';", submissionId);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    public boolean haveSubmitted(String tableId, String studentId){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + tableId + " WHERE student_id = '%s';", studentId);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    //submit assignment
    public void submitAssignment(String tableId, String submissionId, String studentId, String score, String course, String file){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO " + tableId + " (submission_id, student_id, assignment_score, course_id, assignment_file)" +
                        " VALUES('%s', '%s', '%s', '%s', '%s');",
                submissionId,
                studentId,
                score,
                course,
                file);
        db.execSQL(query);
    }

    //get submitted assignment details
    public SubmittedAssignment getSubmittedAssignmentDetails(String tableId, String studentId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + tableId + " WHERE student_id = '%s';", studentId);

        //make null user
        SubmittedAssignment currentAssignment = null;

        //run query
        cursor = db.rawQuery(SQLQuery, null);

        //check again if data exists
        if (cursor.getCount() > 0){

            cursor.moveToFirst();
            currentAssignment = new SubmittedAssignment(
                    cursor.getString(cursor.getColumnIndex("submission_id")),
                    cursor.getString(cursor.getColumnIndex("student_id")),
                    cursor.getString(cursor.getColumnIndex("assignment_score")),
                    cursor.getString(cursor.getColumnIndex("course_id")),
                    cursor.getString(cursor.getColumnIndex("assignment_file"))
            );

        }
        cursor.close();

        return currentAssignment;
    }

    //get all students submitted assignments
    public List<SubmittedAssignment> getAllSubmittedAssignments(String tableId) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + tableId + ";");
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<SubmittedAssignment> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new SubmittedAssignment(
                            cursor.getString(cursor.getColumnIndex("submission_id")),
                            cursor.getString(cursor.getColumnIndex("student_id")),
                            cursor.getString(cursor.getColumnIndex("assignment_score")),
                            cursor.getString(cursor.getColumnIndex("course_id")),
                            cursor.getString(cursor.getColumnIndex("assignment_file"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //score assignment
    public void scoreAssignment(String tableId, String submissionId, String score){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE " + tableId + " SET assignment_score = '%s' WHERE submission_id = '%s';",
                score,
                submissionId);
        db.execSQL(query);
    }











    /*---   messaging   ---*/
    //create folder table
    public void createChatTable(String tableId){
        //init db
        SQLiteDatabase db = getWritableDatabase();

        //create folder query
        db.execSQL(" CREATE TABLE IF NOT EXISTS " + tableId + " (" +
                "\"id\"" + " INTEGER NOT NULL UNIQUE," +
                "\"sender\"" + " TEXT NOT NULL, " +
                "\"message\"" + " TEXT NOT NULL, " +
                "\"is_approved\"" + " TEXT NOT NULL, " +
                "PRIMARY KEY(" + "\"id\" AUTOINCREMENT));");
    }

    //send message
    public void sendMessage(String tableId, String senderId, String message, String isApproved){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO " + tableId + " (sender, message, is_approved) VALUES('%s', '%s', '%s');",
                senderId,
                message,
                isApproved);
        db.execSQL(query);
    }

    //approve and disprove message
    public void changeMessageStatus(String tableId, int messageId, String status){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE " + tableId + " SET is_approved = '%s' WHERE id = '%d';",
                status,
                messageId);
        db.execSQL(query);
    }

    //get all messages
    public List<Chat> getMessages(String tableId) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + tableId + ";");
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<Chat> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new Chat(
                            cursor.getInt(cursor.getColumnIndex("id")),
                            cursor.getString(cursor.getColumnIndex("sender")),
                            cursor.getString(cursor.getColumnIndex("message")),
                            cursor.getString(cursor.getColumnIndex("is_approved"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //delete message
    public void deleteMessage(String tableId, int messageId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM " + tableId + " WHERE id = '%i';", messageId);
        db.execSQL(query);
    }








    /*---   assessment   ---*/
    //check if assessment id is already in use
    public boolean isQuestionIdInUse(String assessmentId){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + ASSESSMENT_TABLE + " WHERE assessment_id = '%s';", assessmentId);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    //check if question has already been set
    public boolean isQuestionAlreadySet(String courseId, String question){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + ASSESSMENT_TABLE + " WHERE course_id = '%s' AND question = '%s';", courseId, question);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    //add question
    public void addNewAssessmentQuestion(String assessmentId, String course, String lecturer, String question, String a, String b, String c, String d, String e, String answer){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO " + ASSESSMENT_TABLE + " (assessment_id, course_id, lecturer_id, question, option_a, option_b, option_c, option_d, option_e, answer)" +
                        " VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                assessmentId,
                course,
                lecturer,
                question,
                a,
                b,
                c,
                d,
                e,
                answer);
        db.execSQL(query);
    }

    //get all questions
    public List<Assessment> getAllAssessment(String courseId) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + ASSESSMENT_TABLE + " WHERE course_id = '%s';", courseId);
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<Assessment> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new Assessment(
                            cursor.getString(cursor.getColumnIndex("assessment_id")),
                            cursor.getString(cursor.getColumnIndex("course_id")),
                            cursor.getString(cursor.getColumnIndex("lecturer_id")),
                            cursor.getString(cursor.getColumnIndex("question")),
                            cursor.getString(cursor.getColumnIndex("option_a")),
                            cursor.getString(cursor.getColumnIndex("option_b")),
                            cursor.getString(cursor.getColumnIndex("option_c")),
                            cursor.getString(cursor.getColumnIndex("option_d")),
                            cursor.getString(cursor.getColumnIndex("option_e")),
                            cursor.getString(cursor.getColumnIndex("answer"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //get assessment for student
    public List<Assessment> getStudentAssessment(String courseId) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + ASSESSMENT_TABLE + " WHERE course_id = '%s' ORDER BY RANDOM() LIMIT 50;", courseId);
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<Assessment> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new Assessment(
                            cursor.getString(cursor.getColumnIndex("assessment_id")),
                            cursor.getString(cursor.getColumnIndex("course_id")),
                            cursor.getString(cursor.getColumnIndex("lecturer_id")),
                            cursor.getString(cursor.getColumnIndex("question")),
                            cursor.getString(cursor.getColumnIndex("option_a")),
                            cursor.getString(cursor.getColumnIndex("option_b")),
                            cursor.getString(cursor.getColumnIndex("option_c")),
                            cursor.getString(cursor.getColumnIndex("option_d")),
                            cursor.getString(cursor.getColumnIndex("option_e")),
                            cursor.getString(cursor.getColumnIndex("answer"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //check student have submitted before
    public boolean hasStudentSubmitted(String studentId, String courseId){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + ASSESSMENT_RESULT_TABLE + " WHERE student_id = '%s' AND course_id = '%s';", studentId, courseId);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    //add result
    public void addAssessmentResult(String resultId, String courseId, String studentId, String attempted, String passed, String failed, String total){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO " + ASSESSMENT_RESULT_TABLE + " (result_id, course_id, student_id, attempted, passed, failed, total)" +
                        " VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                resultId,
                courseId,
                studentId,
                attempted,
                passed,
                failed,
                total);
        db.execSQL(query);
    }

    //get assessment for student
    public List<AssessmentResult> getStudentAssessmentResult(String courseId) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + ASSESSMENT_RESULT_TABLE + " WHERE course_id = '%s';", courseId);
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<AssessmentResult> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new AssessmentResult(
                            cursor.getString(cursor.getColumnIndex("result_id")),
                            cursor.getString(cursor.getColumnIndex("course_id")),
                            cursor.getString(cursor.getColumnIndex("student_id")),
                            cursor.getString(cursor.getColumnIndex("attempted")),
                            cursor.getString(cursor.getColumnIndex("passed")),
                            cursor.getString(cursor.getColumnIndex("failed")),
                            cursor.getString(cursor.getColumnIndex("total"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //get assessment for student
    public List<AssessmentResult> getMyResults(String studentId) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + ASSESSMENT_RESULT_TABLE + " WHERE student_id = '%s';", studentId);
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<AssessmentResult> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new AssessmentResult(
                            cursor.getString(cursor.getColumnIndex("result_id")),
                            cursor.getString(cursor.getColumnIndex("course_id")),
                            cursor.getString(cursor.getColumnIndex("student_id")),
                            cursor.getString(cursor.getColumnIndex("attempted")),
                            cursor.getString(cursor.getColumnIndex("passed")),
                            cursor.getString(cursor.getColumnIndex("failed")),
                            cursor.getString(cursor.getColumnIndex("total"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //get student result
    public AssessmentResult getAssessmentResultDetails(String courseId, String studentId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + ASSESSMENT_RESULT_TABLE + " WHERE student_id = '%s' AND course_id = '%s';", studentId, courseId);

        //make null user
        AssessmentResult currentResult = null;

        //run query
        cursor = db.rawQuery(SQLQuery, null);

        //check again if data exists
        if (cursor.getCount() > 0){

            cursor.moveToFirst();
            currentResult = new AssessmentResult(
                    cursor.getString(cursor.getColumnIndex("result_id")),
                    cursor.getString(cursor.getColumnIndex("course_id")),
                    cursor.getString(cursor.getColumnIndex("student_id")),
                    cursor.getString(cursor.getColumnIndex("attempted")),
                    cursor.getString(cursor.getColumnIndex("passed")),
                    cursor.getString(cursor.getColumnIndex("failed")),
                    cursor.getString(cursor.getColumnIndex("total"))
            );

        }
        cursor.close();

        return currentResult;
    }







    /*---   analytics   ---*/
    //check if student analytics is created
    public boolean isAnalyticsCreated(String studentId){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + ANALYTICS_TABLE + " WHERE student_id = '%s';", studentId);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    //create student analytics row
    public void createStudentAnalytics(String studentId, int courses, int contributions, int assignmentsSeen, int assignmentsDone, int moduleViews, int assessmentsTaken){
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("DefaultLocale") String query = String.format("INSERT INTO " + ANALYTICS_TABLE + " (student_id, courses_registered, group_contributions, assignments_submitted, " +
                        "assignment_viewed, assessments_taken, modules_completed)" +
                        " VALUES('%s', '%d', '%d', '%d', '%d', '%d', '%d');",
                studentId,
                courses,
                contributions,
                assignmentsDone,
                assignmentsSeen,
                assessmentsTaken,
                moduleViews);
        db.execSQL(query);
    }

    //add course registered
    public void updateRegisteredCourses(String studentId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE " + ANALYTICS_TABLE + " SET courses_registered = courses_registered + 1 WHERE student_id = '%s';", studentId);
        db.execSQL(query);
    }

    //add discussion
    public void updateGroupEngagement(String studentId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE " + ANALYTICS_TABLE + " SET group_contributions = group_contributions + 1 WHERE student_id = '%s';", studentId);
        db.execSQL(query);
    }

    //add assignment submission
    public void updateSubmittedEngagement(String studentId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE " + ANALYTICS_TABLE + " SET assignments_submitted = assignments_submitted + 1 WHERE student_id = '%s';", studentId);
        db.execSQL(query);
    }

    //add assignment viewed
    public void updateAssignmentEngagement(String studentId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE " + ANALYTICS_TABLE + " SET assignment_viewed = assignment_viewed + 1 WHERE student_id = '%s';", studentId);
        db.execSQL(query);
    }

    //add assessments taken
    public void updateAssessmentEngagement(String studentId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE " + ANALYTICS_TABLE + " SET assessments_taken = assessments_taken + 1 WHERE student_id = '%s';", studentId);
        db.execSQL(query);
    }

    //add module views
    public void updateModulesEngagement(String studentId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE " + ANALYTICS_TABLE + " SET modules_completed = modules_completed + 1 WHERE student_id = '%s';", studentId);
        db.execSQL(query);
    }

    //get all student analytics
    public Analytics getStudentAnalytics(String studentId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From " + ANALYTICS_TABLE + " WHERE student_id = '%s';", studentId);

        //make null user
        Analytics currentRecord = null;

        //run query
        cursor = db.rawQuery(SQLQuery, null);

        //check again if data exists
        if (cursor.getCount() > 0){

            cursor.moveToFirst();
            currentRecord = new Analytics(
                    cursor.getString(cursor.getColumnIndex("student_id")),
                    cursor.getInt(cursor.getColumnIndex("courses_registered")),
                    cursor.getInt(cursor.getColumnIndex("group_contributions")),
                    cursor.getInt(cursor.getColumnIndex("assignments_submitted")),
                    cursor.getInt(cursor.getColumnIndex("assignment_viewed")),
                    cursor.getInt(cursor.getColumnIndex("assessments_taken")),
                    cursor.getInt(cursor.getColumnIndex("modules_completed"))
            );

        }
        cursor.close();

        return currentRecord;
    }
}
