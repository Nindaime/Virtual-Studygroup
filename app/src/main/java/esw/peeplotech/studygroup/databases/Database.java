package esw.peeplotech.studygroup.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import esw.peeplotech.studygroup.models.Course;
import esw.peeplotech.studygroup.models.SubscribedCourse;
import esw.peeplotech.studygroup.models.User;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "generaldb.db";
    private static final int DB_VER = 1;
    public static final String USER_TABLE = "Users";
    public static final String COURSE_TABLE = "Courses";
    public static final String SUBSCRIBED_COURSE_TABLE = "SubscribedCourses";
    public static final String MODULE_TABLE = "Modules";
    public static final String ASSIGNMENT_TABLE = "Assignments";

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




}
