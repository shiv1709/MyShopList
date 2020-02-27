package com.example.shiv.itemstobuy.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shiv.itemstobuy.R;
import com.example.shiv.itemstobuy.data.DatabaseHandler;
import com.example.shiv.itemstobuy.model.Item;
import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Item> itemList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater layoutInflater;

    //Constructor
    public RecyclerViewAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_row, parent, false);


        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Item item = itemList.get(position); // object Item
        holder.itemName.setText(MessageFormat.format("Item:{0}", item.getItemName()));
        holder.itemQuantity.setText(MessageFormat.format("Qty: {0}", item.getItemQuantity()));
        holder.itemBrand.setText(MessageFormat.format("Brand: {0}", item.getItemBrand()));
        holder.itemColor.setText(MessageFormat.format("Color: {0}", item.getItemColor()));
        holder.itemSize.setText(MessageFormat.format("Size: {0}", item.getItemSize()));
        holder.dateAdded.setText(MessageFormat.format("Added on: {0}", item.getDateItemAdded()));

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView itemName;
        public TextView itemQuantity;
        public TextView itemBrand;
        public TextView itemColor;
        public TextView itemSize;
        public TextView dateAdded;

        public int id; //to store id of each item

        public Button editButton;
        public Button deleteButton;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            itemName = itemView.findViewById(R.id.item_name);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            itemBrand = itemView.findViewById(R.id.item_brand);
            itemColor = itemView.findViewById(R.id.item_color);
            itemSize = itemView.findViewById(R.id.item_size);
            // itemType = itemView.findViewById(R.id.item_type);
            dateAdded = itemView.findViewById(R.id.item_date);

            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            Item item = itemList.get(position);

            switch (view.getId()) {
                case R.id.editButton:
                    //edit item
                    editItem(item);
                    break;

                case R.id.deleteButton:
                    //delete item
                    deleteItem(item.getId());
                    break;
            }
        }

        private void deleteItem(final int id) {

            builder = new AlertDialog.Builder(context);
            layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.delete_confirmation_popup, null);

            Button okButton = view.findViewById(R.id.ok_option);
            Button cancelButton = view.findViewById(R.id.cancel_option);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteItemFromDb(id);
                    itemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    dialog.dismiss();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

        }

        private void editItem(final Item newItem) {

            //Todo: populate the popup with current object data !

            TextView title;
            Button saveButton;
            builder = new AlertDialog.Builder(context);
            layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.popup, null);

            itemName = view.findViewById(R.id.itemNameToBuy);
            itemQuantity = view.findViewById(R.id.itemQuantityToBuy);
            itemBrand = view.findViewById(R.id.itemBrandToBuy);
            itemColor = view.findViewById(R.id.itemColorToBuy);
            itemSize = view.findViewById(R.id.itemSizeToBuy);
            title = view.findViewById(R.id.title);
            saveButton = view.findViewById(R.id.save_btn);

            title.setText(R.string.edit_item_text);
            itemName.setText(newItem.getItemName());
            itemQuantity.setText(newItem.getItemQuantity());
            itemBrand.setText(newItem.getItemBrand());
            itemColor.setText(newItem.getItemColor());
            itemSize.setText(newItem.getItemSize());
            saveButton.setText(R.string.update_btn_text);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //update our item
                    DatabaseHandler databaseHandler = new DatabaseHandler(context);

                    // update item details
                    newItem.setItemName(itemName.getText().toString());
                    newItem.setItemQuantity(itemQuantity.getText().toString());
                    newItem.setItemBrand(itemBrand.getText().toString());
                    newItem.setItemColor(itemColor.getText().toString());
                    newItem.setItemSize(itemSize.getText().toString());

                    if (!itemName.getText().toString().isEmpty() &&
                            !itemQuantity.getText().toString().isEmpty()) {

                        boolean isupdated = databaseHandler.updateItemIntoDb(newItem);
                        if (isupdated)
                            Snackbar.make(view, "Item Updated successfully", Snackbar.LENGTH_SHORT).show();
                        else
                            Snackbar.make(view, "Item unable to update due to some exception", Snackbar.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //code to be run
                                dialog.dismiss();

                                //Todo: move to nex screen  - details screen

                                Intent i = new Intent();
                                i.setClassName("com.example.shiv.itemstobuy",
                                        "com.example.shiv.itemstobuy.ListActivity");
                                context.startActivity(i);
               /* Intent intent = new Intent(getApplicationContext(),ListActivity.class);

                startActivity(intent);*/ //we have to pass intent of Activity to start here
                            }
                        }, 1200); //1200 ms it will wait,Time in miliseconds here for delay
                        notifyItemChanged(getAdapterPosition(), newItem); //important
                    }
                    else {
                        Snackbar.make(view, "Either Item Name or Quantity Field is empty",
                                Snackbar.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }
}
