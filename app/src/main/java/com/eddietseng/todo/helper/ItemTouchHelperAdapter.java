package com.eddietseng.todo.helper;

/**
 *
 */
public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    void onItemMarkDone(int position);
}
