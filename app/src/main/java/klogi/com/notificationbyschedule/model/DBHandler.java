package klogi.com.notificationbyschedule.model;


/**
 * Created by alaeddine on 13/04/16.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION =13;
    // Database Name
    private static final String DATABASE_NAME = "notification";
    // Contacts table name
    private static final String TABLE_SERVER = "servers";
    // Shops Table Columns names

    private static final String KEY_IPSERVER = "ipserver";
    private static final String KEY_PORTSERVER = "portserver";
    private static final String KEY_PROFONDEURsERVER = "profondeurserver";
    private static  final String KEY_IPSERVERS_SECONDAIRE="ipserversecondaire";


    // Contacts table name
    private static final String TABLE_USERS = "users";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_HOURWORK = "hourWork";
    private static final String KEY_FREAQ= "freq";
    private static final String KEY_DATEFIN = "dateFin";

    private static final String KEY_MODIFICATION = "modification";
    private static final String KEY_ACQUISITION = "acquisition";



  //  GPS table name
    private static final String TABLE_GPS = "gps";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_RELEVEHORAIRE="releveHoraire";
    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT,"
                + KEY_LOGIN + " TEXT NOT NULL UNIQUE," //NOT NULL UNIQUE
                + KEY_PASSWORD + " TEXT NOT NULL UNIQUE," //NOT NULL UNIQUE
                + KEY_HOURWORK + " TEXT,"
                + KEY_FREAQ + " TEXT,"
                + KEY_DATEFIN + " TEXT,"
                + KEY_MODIFICATION+ " TEXT,"
                + KEY_ACQUISITION + " TEXT )"; //" , "+KEY_LOGIN+" , "+KEY_PASSWORD+
        try {
            db.execSQL(CREATE_USERS_TABLE);
        }catch (Exception e){
            Log.d("Exception1 :",e.getMessage());
        }
        String CREATE_SERVER = "CREATE TABLE " + TABLE_SERVER + "("
                + KEY_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT,"
                + KEY_IPSERVER + " TEXT NOT NULL UNIQUE," //NOT NULL UNIQUE
                + KEY_PORTSERVER + " TEXT NOT NULL UNIQUE," //NOT NULL UNIQUE
                + KEY_IPSERVERS_SECONDAIRE + " TEXT,"
                + KEY_PROFONDEURsERVER + " TEXT )";
        try {
            db.execSQL(CREATE_SERVER);
        }catch (Exception e){
            Log.d("Exception1 :",e.getMessage());
        }


        String CREATE_GPS = "CREATE TABLE " + TABLE_GPS + "("
                + KEY_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT,"
                + KEY_LATITUDE + " TEXT ,"
                + KEY_RELEVEHORAIRE + " TEXT ,"
                + KEY_LONGITUDE + " TEXT )";
        try {
            db.execSQL(CREATE_GPS);
        }catch (Exception e){
            Log.d("Exception1 :",e.getMessage());
        }

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        if (newVersion>oldVersion){

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPS);
// Creating tables again
            onCreate(db);
        }
    }
    // Adding new GPS
    public void addGPS(GPS gps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LATITUDE, gps.getLatitude()); // User Name
        values.put(KEY_LONGITUDE,gps.getLongitude() ); // User Surname
        values.put(KEY_RELEVEHORAIRE,gps.getReleveHoraire());


        try {
// Inserting Row
            db.insert(TABLE_GPS, null, values);
        }catch (Exception e){
            Log.d("Exception2 :",e.getMessage());
        }
        db.close(); // Closing database connection
    }

    // Adding new SEVER
    public void addServer(ServerConnection serverConnection) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IPSERVER, serverConnection.getIpServer()); // User Name
        values.put(KEY_PORTSERVER, serverConnection.getPortServer()); // User Surname
       values.put(KEY_IPSERVERS_SECONDAIRE,serverConnection.getIpServerSecondaire());
        values.put(KEY_PROFONDEURsERVER,serverConnection.getProfondeurServer()); // User Email

        try {
// Inserting Row
            db.insert(TABLE_SERVER, null, values);
        }catch (Exception e){
            Log.d("Exception2 :",e.getMessage());
        }
        db.close(); // Closing database connection
    }
    // Adding new user
    public void addUser(Users user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOGIN, user.getLogin()); // User Name
        values.put(KEY_PASSWORD, user.getPassword()); // User Surname
        values.put(KEY_HOURWORK,user.getHourWork()); // User Email
        values.put(KEY_FREAQ,user.getFreq()); // User Password
        values.put(KEY_DATEFIN, user.getDateFin()); // User Surname
        values.put(KEY_MODIFICATION, user.getModification()); // User Surname
        values.put(KEY_ACQUISITION, user.getAcquisition()); // User Surname
        try {
// Inserting Row
            db.insert(TABLE_USERS, null, values);
        }catch (Exception e){
            Log.d("Exception2 :",e.getMessage());
        }
        db.close(); // Closing database connection
    }
    // Getting one User by Login and Password
    public Users getUserByLoginAndPassword(String login,String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[] { KEY_ID,
                        KEY_LOGIN, KEY_PASSWORD ,KEY_HOURWORK,KEY_FREAQ,KEY_DATEFIN,KEY_MODIFICATION,KEY_ACQUISITION}, KEY_LOGIN + "=? and "+KEY_PASSWORD+"=?",
                new String[] {login,password }, null, null, null, null);
        if (cursor != null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            Users user = new Users(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7));
// return user
            return user;
        }else {
            return null;
        }
    }
    // Updating a User
    public int updateUser(Users user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_HOURWORK,user.getHourWork()); // User Email
        values.put(KEY_FREAQ,user.getFreq()); // User Password
        values.put(KEY_DATEFIN, user.getDateFin()); // User Surname
        values.put(KEY_MODIFICATION, user.getModification()); // User Surname
        values.put(KEY_ACQUISITION, user.getAcquisition()); // User Surname
// updating row
        return db.update(TABLE_USERS, values,KEY_LOGIN + " = ? and "+KEY_PASSWORD+"= ?", new String[] { user.getLogin(),user.getPassword() });
    }

    // Deleting a user
    public void deleteUser(String login ,String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_USERS,null, null);
        db.delete(TABLE_USERS, KEY_LOGIN + " = ? and "+KEY_PASSWORD+"= ?", new String[] { login,password });
        db.close();
    }
    // Deleting a user
    public void deleteGPS() {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_USERS,null, null);
        db.delete(TABLE_GPS, null,null);
        db.close();
    }
    // Deleting a user
    public void deleteServer() {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_USERS,null, null);
        db.delete(TABLE_SERVER, null,null);
        db.close();
    }
    // Deleting a user
    public void deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_USERS,null, null);
        db.delete(TABLE_USERS, null,null);
        db.close();
    }
    public GPS getGPS() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GPS, new String[] {
                        KEY_LATITUDE, KEY_LONGITUDE,KEY_RELEVEHORAIRE }, null,
                null, null, null, null, null);

        if (cursor != null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            GPS gps = new GPS(cursor.getString(0), cursor.getString(1), cursor.getString(2));
// return user
            db.close(); // Closing database connection
            return gps;
        }else {
            db.close(); // Closing database connection
            return null;
        }
    }

    public ServerConnection getServer() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SERVER, new String[] { KEY_ID,
                        KEY_IPSERVER, KEY_PORTSERVER ,KEY_PROFONDEURsERVER,KEY_IPSERVERS_SECONDAIRE}, null,
                null, null, null, null, null);
        if (cursor != null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            ServerConnection serverConnection = new ServerConnection(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3),cursor.getString(4));
// return user
            return serverConnection;
        }else {
            return null;
        }
    }
    public Users getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[] { KEY_ID,
                        KEY_LOGIN, KEY_PASSWORD ,KEY_HOURWORK,KEY_FREAQ,KEY_DATEFIN,KEY_MODIFICATION,KEY_ACQUISITION}, null,
                null, null, null, null, null);
        if (cursor != null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            Users user = new Users(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7));
// return user
            return user;
        }else {
            return null;
        }
    }

}