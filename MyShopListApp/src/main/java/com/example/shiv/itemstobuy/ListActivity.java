package com.example.shiv.itemstobuy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.shiv.itemstobuy.data.DatabaseHandler;
import com.example.shiv.itemstobuy.model.Item;
import com.example.shiv.itemstobuy.ui.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private static final String TAG = "ListActivity" ;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Item> itemList;
    private DatabaseHandler databaseHandler;
    private FloatingActionButton fab;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Button saveButton;
    private EditText itemName;
    private EditText itemQuantity;
    private EditText itemBrand;
    private EditText itemSize;
    private EditText itemColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        databaseHandler = new DatabaseHandler(this);

        recyclerView = findViewById(R.id.recyviewItemslist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();

        //Get Items from db
        itemList = databaseHandler.getAllItemsFromDb();

        for(Item item : itemList){
            Log.d(TAG,"onCreate : " +item.getItemName());
        }

        recyclerViewAdapter = new RecyclerViewAdapter(this,itemList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

        fab = findViewById(R.id.fab_list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopupDialogForItem();
/*                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

    }

    private void createPopupDialogForItem(){

        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup,null);

        itemName = view.findViewById(R.id.itemNameToBuy);
        itemQuantity = view.findViewById(R.id.itemQuantityToBuy);
        itemBrand = view.findViewById(R.id.itemBrandToBuy);
        itemColor = view.findViewById(R.id.itemColorToBuy);
        itemSize = view.findViewById(R.id.itemSizeToBuy);
        //itemType = view.findViewById(R.id.itemTypeToBuy);
        saveButton = view.findViewById(R.id.save_btn);

        builder.setView(view);
        dialog = builder.create();//creating our dialog object
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!itemName.getText().toString().isEmpty()
                        && !itemQuantity.getText().toString().isEmpty()) {
                    saveItem(view);
                } else {
                    Snackbar.make(view, "Empty field of Item Name or Quantity not allowed", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveItem(View view){

        //We are passing a view here in saveItem bcoz Snackbar needed a view to show msg

        //Todo: Save item to db
        Item item = new Item();
        item.setItemName(itemName.getText().toString().trim());
        item.setItemQuantity(itemQuantity.getText().toString().trim());
        item.setItemBrand(itemBrand.getText().toString().trim());
        item.setItemColor(itemColor.getText().toString().trim());
        item.setItemSize(itemSize.getText().toString().trim());
        //item.setItemType(itemType.getText().toString().trim());

        boolean isInserted = databaseHandler.insertItemIntoDb(item);

        if (isInserted)
            Snackbar.make(view, "Item Saved successfully", Snackbar.LENGTH_SHORT).show();
        else
            Snackbar.make(view, "Item unable to save due to some exception", Snackbar.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //code to be run
                dialog.dismiss();

                //Todo: move to nex screen  - details screen

                Intent i = new Intent();
                i.setClassName("com.example.shiv.itemstobuy",
                        "com.example.shiv.itemstobuy.ListActivity");
                startActivity(i);
               /* Intent intent = new Intent(getApplicationContext(),ListActivity.class);

                startActivity(intent);*/ //we have to pass intent of Activity to start here
            }
        },1200); //1200 ms it will wait,Time in miliseconds here for delay
    }
}
