package com.eddietseng.todo;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eddietseng.todo.helper.ItemTouchHelperAdapter;
import com.eddietseng.todo.helper.ItemTouchHelperViewHolder;
import com.eddietseng.todo.helper.RecyclerViewClickListener;
import com.eddietseng.todo.sqlite.TodoItemDatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    private Context context;
    private ArrayList<TodoItem> mValues;
    private static RecyclerViewClickListener itemListener;

    public MyItemRecyclerViewAdapter(Context context, ArrayList<TodoItem> list,
                                     RecyclerViewClickListener itemListener ) {
        this.context = context;
        mValues = list;
        this.itemListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.description.setText(mValues.get(position).getDescription());
        switch(mValues.get(position).getPriority()){
            case 0:
                holder.priority.setText("HIGH");
                holder.priority.setTextColor( ContextCompat.getColor( context,R.color.colorPriorityHigh));
                break;

            case 1:
                holder.priority.setText("MEDIUM");
                holder.priority.setTextColor( ContextCompat.getColor( context,R.color.colorPriorityMedium));
                break;

            case 2:
                holder.priority.setText("LOW");
                holder.priority.setTextColor( ContextCompat.getColor( context,R.color.colorPriorityLow));
                break;

            default:
                break;
        }
        holder.date.setText(mValues.get(position).getDate());
    }

    @Override
    public void onItemDismiss(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);

        TodoItemDatabaseHelper databaseHelper = TodoItemDatabaseHelper.getInstance(context);
        databaseHelper.updateAllTodoItems(mValues);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        TodoItem prev = mValues.remove(fromPosition);
        mValues.add(toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);

        TodoItemDatabaseHelper databaseHelper = TodoItemDatabaseHelper.getInstance(context);
        databaseHelper.updateAllTodoItems(mValues);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void add(TodoItem item, int position) {
        mValues.add(position, item);
        notifyItemInserted(position);

        TodoItemDatabaseHelper databaseHelper = TodoItemDatabaseHelper.getInstance(context);
        databaseHelper.updateAllTodoItems(mValues);
    }

    public void remove(TodoItem item) {
        int position = mValues.indexOf(item);
        mValues.remove(position);
        notifyItemRemoved(position);

        TodoItemDatabaseHelper databaseHelper = TodoItemDatabaseHelper.getInstance(context);
        databaseHelper.updateAllTodoItems(mValues);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder, View.OnClickListener {

        public TextView description, priority, date;

        public ViewHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.description);
            priority = (TextView) itemView.findViewById(R.id.priority);
            date = (TextView) itemView.findViewById(R.id.date);
            itemView.setOnClickListener( this );
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v, this.getAdapterPosition());
        }

    }
}
