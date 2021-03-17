package esw.peeplotech.studygroup.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import esw.peeplotech.studygroup.models.Assignment;
import esw.peeplotech.studygroup.models.Chat;
import esw.peeplotech.studygroup.models.Course;
import esw.peeplotech.studygroup.models.Module;
import esw.peeplotech.studygroup.models.SubmittedAssignment;
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


}
