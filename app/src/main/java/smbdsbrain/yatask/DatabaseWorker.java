package smbdsbrain.yatask;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Paul on 4/15/2017.
 */

public class DatabaseWorker extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "userdata.db";
    private static final int DATABASE_VERSION = 1;

    Context context;
    public DatabaseWorker(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public DatabaseWorker(Context context, String name, SQLiteDatabase.CursorFactory factory,
                              int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS favorites(" +
                                                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                                                "originalText TEXT," +
                                                "translatedText TEXT," +
                                                "langs TEXT," +
                                                "hashCode INT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS history(" +
                                                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                                                "originalText TEXT," +
                                                "translatedText TEXT," +
                                                "langs TEXT," +
                                                "hashCode INT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS cache(" +
                                                        "request TEXT," +
                                                        "originalText TEXT," +
                                                        "translatedText TEXT," +
                                                        "langs TEXT" +
                                                        ")");
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DatabaseErrorHandler eh = new DatabaseErrorHandler() {
            @Override
            public void onCorruption(SQLiteDatabase dbObj) {
                //TODO: add hundler
                return;
            }
        };
    }

    public boolean addToHistory(Translation translation) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("DELETE FROM history WHERE hashCode = ?", new String[] {Integer.toString(translation.hashCode())});

        String query = "INSERT INTO history (originalText, translatedText, langs, hashCode) VALUES (?, ?, ?, ?)";
        db.execSQL(query, new String[]{translation.getOriginalText(), translation.getTranslatedText(), translation.getTranslateLangs(), Integer.toString(translation.hashCode())});
        db.close();
        return true;

    }

    public boolean addToFavorite(Translation translation) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM favorites WHERE hashCode = ?", new String[] {Integer.toString(translation.hashCode())});
        if(cur.getCount() == 0) {
            String query = "INSERT INTO favorites (originalText, translatedText, langs, hashCode) VALUES (?, ?, ?, ?)";
            db.execSQL(query, new String[]{translation.getOriginalText(), translation.getTranslatedText(), translation.getTranslateLangs(), Integer.toString(translation.hashCode())});
            db.close();
            return true;
        }
        else
        {
            String query = "DELETE FROM favorites WHERE hashCode = ?";
            db.execSQL(query, new String[]{Integer.toString(translation.hashCode())});
            db.close();
            return false;
        }
    }

    public int[] getFavHashCodes() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT hashCode FROM favorites ORDER BY hashCode ASC", null);
        int[] ans = new int[cur.getCount()];
        if(cur.getCount() == 0)
            return ans;
        int i = 0;
        cur.moveToFirst();
        do {
            ans[i++] = cur.getInt(cur.getColumnIndex("hashCode"));
        } while (cur.moveToNext());
        cur.close();
        db.close();
        return ans;
    }

    public boolean addToCache(String request, Translation translation) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "INSERT INTO cache (request, originalText, translatedText, langs) VALUES (?, ?, ?, ?)";
        db.execSQL(query, new String[]{request, translation.getOriginalText(), translation.getTranslatedText(), translation.getTranslateLangs()});
        db.close();
        return true;

    }

    public ArrayList<Translation> getCache(String request, String langs) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM cache WHERE request=? AND langs=?";
        Cursor cur = db.rawQuery(query, new String[]{request, langs});

        ArrayList<Translation> ans = new ArrayList<>();

        if(cur.getCount() == 0)
            return ans;

        cur.moveToFirst();
        do {
            ans.add(new Translation(cur.getString(cur.getColumnIndex("originalText")), cur.getString(cur.getColumnIndex("translatedText")), cur.getString(cur.getColumnIndex("langs"))));
        } while(cur.moveToNext());
        cur.close();
        db.close();
        return  ans;
    }

    public ArrayList<Translation> getHistory() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM history ORDER BY timestamp DESC";
        Cursor cur = db.rawQuery(query, null);

        ArrayList<Translation> ans = new ArrayList<>();

        if(cur.getCount() == 0)
            return ans;

        cur.moveToFirst();
        do {
            ans.add(new Translation(cur.getString(cur.getColumnIndex("originalText")), cur.getString(cur.getColumnIndex("translatedText")), cur.getString(cur.getColumnIndex("langs"))));
        } while(cur.moveToNext());
        cur.close();
        db.close();
        return  ans;
    }

    public ArrayList<Translation> getFavorites() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM favorites ORDER BY timestamp DESC";
        Cursor cur = db.rawQuery(query, null);

        ArrayList<Translation> ans = new ArrayList<>();

        if(cur.getCount() == 0)
            return ans;

        cur.moveToFirst();
        do {
            ans.add(new Translation(cur.getString(cur.getColumnIndex("originalText")), cur.getString(cur.getColumnIndex("translatedText")), cur.getString(cur.getColumnIndex("langs"))));
        } while(cur.moveToNext());
        cur.close();
        db.close();
        return  ans;
    }
}