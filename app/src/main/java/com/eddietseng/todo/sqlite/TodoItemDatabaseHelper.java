package com.eddietseng.todo.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eddietseng.todo.TodoItem;

import java.util.ArrayList;

/**
 * SQLite database
 * TODO Need to improve this with unique id instead of position
 */
public class TodoItemDatabaseHelper extends SQLiteOpenHelper{
    // Database Info
    private static final String DATABASE_NAME = "todosDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_TODOS = "todos";

    // Table columns
    private static final String TODO_ID = "id";
    private static final String TODO_DES = "description";
    private static final String TODO_DATE = "date";
    private static final String TODO_PRIORITY = "priority";

    private static TodoItemDatabaseHelper sInstance;

    public static synchronized TodoItemDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TodoItemDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private TodoItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODOS_TABLE = "CREATE TABLE " + TABLE_TODOS +
                "(" +
                    TODO_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                    TODO_DES + " TEXT," + // Define a foreign key
                    TODO_DATE + " TEXT," +
                    TODO_PRIORITY + " INTEGER" +
                ")";

        db.execSQL(CREATE_TODOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);
            onCreate(db);
        }
    }

    // Insert a todoItem into the database
    public void addTodoItem(TodoItem item) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(TODO_DES, item.getDescription());
            values.put(TODO_DATE, item.getDate());
            values.put(TODO_PRIORITY, item.getPriority());

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_TODOS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("TodoItemDatabaseHelper", "Error while trying to add todoitem to database");
        } finally {
            db.endTransaction();
        }
    }

    // Get all todoItems in the database
    public ArrayList<TodoItem> getAllTodoItems() {
        ArrayList<TodoItem> items = new ArrayList<TodoItem>();

        String POSTS_SELECT_QUERY = String.format("SELECT * FROM %s", TABLE_TODOS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    TodoItem item = new TodoItem();
                    Log.i( "db" , "id= " + cursor.getString(cursor.getColumnIndex(TODO_ID)) );
                    item.setDescription(cursor.getString(cursor.getColumnIndex(TODO_DES)));
                    item.setDate(cursor.getString(cursor.getColumnIndex(TODO_DATE)));
                    item.setPriority(cursor.getInt(cursor.getColumnIndex(TODO_PRIORITY)));

                    items.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("TodoItemDatabaseHelper", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return items;
    }

    // Delete all todoItems in the database
    public void deleteAllTodoItems() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_TODOS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("TodoItemDatabaseHelper", "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }

    // Update all todoItems in the database
    public void updateAllTodoItems( ArrayList<TodoItem> items) {
        deleteAllTodoItems();
        for( TodoItem item: items) {
            addTodoItem(item);
        }
    }

    // Update todoItems in the database
    public void updateTodoItem( int position, TodoItem item) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(TODO_DES, item.getDescription());
            values.put(TODO_DATE, item.getDate());
            values.put(TODO_PRIORITY, item.getPriority());

            db.update(TABLE_TODOS, values, TODO_ID +"="+(position+1), null);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("TodoItemDatabaseHelper", "Error while trying to update todoitem to database");
        } finally {
            db.endTransaction();
        }
    }
}
