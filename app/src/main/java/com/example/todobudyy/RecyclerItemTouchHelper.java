package com.example.todobudyy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todobudyy.Adaptor.ToDoAdaptor;
import com.example.todobudyy.Model.ToDoModel;


import java.util.ConcurrentModificationException;
import java.util.List;

public class RecyclerItemTouchHelper  extends ItemTouchHelper.SimpleCallback {
    private ToDoAdaptor adapter;
    private SQLiteDatabase db;
    public RecyclerItemTouchHelper(ToDoAdaptor adaptor)
    {
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter=adaptor;

    }
    @Override
    public boolean onMove(RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder,RecyclerView.ViewHolder target)
    {
        return false;
    }
    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder,int direction)
    {
        final int position=viewHolder.getAdapterPosition();
        if(direction==ItemTouchHelper.LEFT)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(adapter.getContext());
            builder.setTitle("Delete Task");
            builder.setMessage("Are you sure you want to delete this task?");
            builder.setPositiveButton("Conform", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.deleteItem(position);
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
        }else
        {
            // Get the task item at the current position
            ToDoModel item = adapter.getItem(position);

            // Create a bundle to pass data to the AddNewTask dialog
            Bundle bundle = new Bundle();
            bundle.putInt("id", item.getId());
            bundle.putString("task", item.getTask());

            // Create a new instance of AddNewTask fragment
            AddNewTask fragment = new AddNewTask();
            fragment.setArguments(bundle);

            // Show the AddNewTask dialog to edit the task using the context
            FragmentManager fragmentManager = ((AppCompatActivity) adapter.getContext()).getSupportFragmentManager();
            fragment.show(fragmentManager, AddNewTask.TAG);

        }

    }
    @Override
    public void onChildDraw(Canvas c,RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder,float dX,float dY,int actionState,boolean isCurrentlyActive)
    {
        super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive);

        Drawable icon;
        ColorDrawable background;

        View itemView=viewHolder.itemView;
        int backgroundCornerOffset=20;

        if(dX>0)
        {
            icon= ContextCompat.getDrawable(adapter.getContext(),R.drawable.ic_edit);
            background=new ColorDrawable(ContextCompat.getColor(adapter.getContext(),R.color.colorPrimaryDark));
        }
        else {
            icon= ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_delete);
            background=new ColorDrawable(Color.RED);
        }

        int iconMargin=(itemView.getHeight() - icon.getIntrinsicHeight())/2;
        int iconTop=itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight())/2;
        int iconBottom=iconTop+icon.getIntrinsicHeight();

        if(dX>0)//swap to right
        {
            int iconLeft=itemView.getLeft()+iconMargin;
            int iconRight=itemView.getLeft()+iconMargin+icon.getIntrinsicHeight();
            icon.setBounds(iconLeft,iconTop,iconRight,iconBottom );

            background.setBounds(itemView.getLeft(),itemView.getTop(),
                    itemView.getLeft()+((int)dX)+backgroundCornerOffset,itemView.getBottom());

        }else if (dX<0)//swiping to the left
        {
            int iconLeft=itemView.getRight()-iconMargin-icon.getIntrinsicWidth();
            int iconRight=itemView.getRight()+iconMargin;
            icon.setBounds(iconLeft,iconTop,iconRight,iconBottom );

            background.setBounds(itemView.getRight()+((int)dX)-backgroundCornerOffset,itemView.getTop(),
                    itemView.getRight(),itemView.getBottom());


        }
        else {
            background.setBounds(0,0,0,0);
        }
        background.draw(c);
        icon.draw(c);
    }





}
