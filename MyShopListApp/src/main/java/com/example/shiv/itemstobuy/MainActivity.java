package com.example.shiv.itemstobuy;

import android.content.Intent;
import android.os.Bundle;

import com.example.shiv.itemstobuy.data.DatabaseHandler;
import com.example.shiv.itemstobuy.model.Item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Button saveButton;
    private EditText itemName;
    private EditText itemQuantity;
    private EditText itemBrand;
    private EditText itemSize;
    private EditText itemColor;
    //private EditText itemType;
    private DatabaseHandler databaseHandler;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHandler = new DatabaseHandler(this);

        byPassActivity();

        //check if item was saved

        List<Item> itemList = databaseHandler.getAllItemsFromDb();
        for(Item item : itemList){
            Log.d(TAG," onCreate " + item.getItemName());
        }

        for(Item item : itemList){
            Log.d(TAG," onCreate " + item.getDateItemAdded());
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopupDialogForItem();
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
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

    private void byPassActivity(){
        if(databaseHandler.getItemCount() > 0){
            startActivity(new Intent(MainActivity.this,ListActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
