package esw.peeplotech.studygroup.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import esw.peeplotech.studygroup.models.User;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "generaldb.db";
    private static final int DB_VER = 1;
    public static final String USER_TABLE = "Users";
    public static final String COURSE_TABLE = "Courses";
    public static final String MODULE_TABLE = "Modules";
    public static final String ASSIGNMENT_TABLE = "Assignments";

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

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

}
