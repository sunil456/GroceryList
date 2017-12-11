package com.androidprojects.sunilsharma.grocerylist.Data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.androidprojects.sunilsharma.grocerylist.Model.Grocery;
import com.androidprojects.sunilsharma.grocerylist.Util.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sunil sharma on 11/21/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper
{
   private Context ctx;
    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     * @param name    of the database file, or null for an in-memory database
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    public DatabaseHandler(Context context)
    {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.ctx = context;
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_GROCERY_TABLE = "CREATE TABLE " + Constants.TABLE_NAME
                + "("
                + Constants.KEY_ID + " INTEGER PRIMARY KEY, "
                + Constants.KEY_GROCERY_ITEM + " TEXT, "
                + Constants.KEY_QTY_NUMBER + " TEXT, "
                + Constants.KEY_DATE_NAME + " LONG);";

        db.execSQL(CREATE_GROCERY_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        onCreate(db);
    }


    public void addGrocery(Grocery grocery)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.KEY_GROCERY_ITEM , grocery.getName());
        values.put(Constants.KEY_QTY_NUMBER , grocery.getQuantity());
        values.put(Constants.KEY_DATE_NAME , java.lang.System.currentTimeMillis());

        //Insert the ROW
        db.insert(Constants.TABLE_NAME , null , values);

        Log.d("Saved!!" , "Saved To DB");

    }

    public Grocery getGrocery(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME ,
                new String[]{
                    Constants.KEY_ID ,
                    Constants.KEY_GROCERY_ITEM ,
                    Constants.KEY_QTY_NUMBER ,
                    Constants.KEY_DATE_NAME
                }, Constants.KEY_ID + "=?" ,
                new String[]{String.valueOf(id)} ,
                null , null , null , null);


        if(cursor != null)
            cursor.moveToFirst();

        Grocery grocery = new Grocery();
        grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
        grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM)));
        grocery.setQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_QTY_NUMBER)));

        java.text.DateFormat dateFormat = java.text.DateFormat.getTimeInstance();
        String formatedDate = dateFormat.
                    format(
                            new Date(
                                    cursor.getLong(
                                            cursor.getColumnIndex(Constants.KEY_DATE_NAME))).
                                    getTime());

            grocery.setDateItemAdded(formatedDate);


        return grocery;
    }

    public List<Grocery> getAllGroceries()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Grocery> groceryList = new ArrayList<>();

        Cursor cursor = db.query(Constants.TABLE_NAME ,
                new String[]
                        {
                                Constants.KEY_ID ,
                                Constants.KEY_GROCERY_ITEM ,
                                Constants.KEY_QTY_NUMBER ,
                                Constants.KEY_DATE_NAME
                        } , null , null , null , null ,
                Constants.KEY_DATE_NAME + " DESC");

        if(cursor.moveToFirst())
        {
            do
            {
                Grocery grocery = new Grocery();

                grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM)));
                grocery.setQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_QTY_NUMBER)));

                java.text.DateFormat dateFormat = java.text.DateFormat.getTimeInstance();
                String formatedDate = dateFormat.
                        format(
                                new Date(
                                        cursor.getLong(
                                                cursor.getColumnIndex(Constants.KEY_DATE_NAME))).
                                        getTime());

                grocery.setDateItemAdded(formatedDate);

                groceryList.add(grocery);

            }while (cursor.moveToNext());
        }
        return groceryList;
    }

    public int updateGrocery(Grocery grocery)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Constants.KEY_GROCERY_ITEM , grocery.getName());
        values.put(Constants.KEY_QTY_NUMBER , grocery.getQuantity());
        values.put(Constants.KEY_DATE_NAME , java.lang.System.currentTimeMillis());


        return db.update(Constants.TABLE_NAME ,
                values ,
                Constants.KEY_ID + "=?" ,
                new String[]{String.valueOf(grocery.getId())} );
    }

    public void deleteGrocery(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME , Constants.KEY_ID + "=?" ,
                new String[]
                        {
                                String.valueOf(id)
                        });

        db.close();
    }

    public int getGroceriesCount()
    {
        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery , null);

        return cursor.getCount();
    }
}
