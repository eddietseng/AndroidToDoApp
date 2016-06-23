package com.eddietseng.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.eddietseng.todo.helper.DividerItemDecoration;
import com.eddietseng.todo.helper.RecyclerViewClickListener;
import com.eddietseng.todo.helper.SimpleItemTouchHelperCallback;
import com.eddietseng.todo.sqlite.TodoItemDatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener{
    // Adapter allows us to display the contents of an ArrayList within a ListView
    MyItemRecyclerViewAdapter adapter;
    ArrayList<TodoItem> items;
    RecyclerView rvItems;
    private ItemTouchHelper mItemTouchHelper;
    private TodoItemDatabaseHelper databaseHelper;
    private final int REQUEST_CODE = 20;
    private int editPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvItems = (RecyclerView)findViewById(R.id.recycler_view);

        readItems();

        adapter = new MyItemRecyclerViewAdapter(this, items, this);

        RecyclerView recyclerView = (RecyclerView) rvItems.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void recyclerViewListClicked(View v, int position){
        Log.i( "recyclerViewListClicked" , "view: " + v.toString() );
        Log.i( "Click listener" , "selected  position: " + position );
        Log.i( "item list" , "list: " + Arrays.toString(items.toArray()) );

        editPosition = position;

        Intent editIntent = new Intent( this, EditItemActivity.class );
        editIntent.putExtra("item", items.get(position) );

        startActivityForResult(editIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            TodoItem item = (TodoItem)data.getExtras().getSerializable("item");
            Log.i( "after edit" , "item: " + (item == null? "NONE":item.toString()) );

            if( item != null ) {
                TodoItem orgItem = items.get(editPosition);
                orgItem.setDescription(item.getDescription());
                orgItem.setDate(item.getDate());
                orgItem.setPriority(item.getPriority());
                adapter.notifyDataSetChanged();

                Log.i( "after update" , "edit position" + editPosition );
                databaseHelper.updateTodoItem(editPosition, orgItem);
                Log.i( "after update" , "" );
            }
        }
    }

    /**
     * Reads item from the DB.
     */
    private void readItems() {
        if(databaseHelper == null)
            databaseHelper = TodoItemDatabaseHelper.getInstance(this);

        items = databaseHelper.getAllTodoItems();
    }

    /**
     * Writes item to the DB.
     */
    private void writeItems() {
        if(databaseHelper == null)
            databaseHelper = TodoItemDatabaseHelper.getInstance(this);

        databaseHelper.updateAllTodoItems(items);
    }

    /**
     * Add text from EditTextField to the list and set the text file back to ""
     * when the button is clicked.
     */
    public void onAddItem(View view) {
        EditText etNewItem = (EditText)findViewById(R.id.etNewItem);
        if( etNewItem != null && etNewItem.getText().toString().length() >0 ) {
            String itemText = etNewItem.getText().toString();
            TodoItem item = new TodoItem();
            item.setDescription(itemText);
            adapter.add(item, adapter.getItemCount());
            etNewItem.setText("");
        }
    }
}
