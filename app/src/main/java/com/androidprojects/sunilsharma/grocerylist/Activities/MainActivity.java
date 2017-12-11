package com.androidprojects.sunilsharma.grocerylist.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidprojects.sunilsharma.grocerylist.Data.DatabaseHandler;
import com.androidprojects.sunilsharma.grocerylist.Model.Grocery;
import com.androidprojects.sunilsharma.grocerylist.R;

public class MainActivity extends AppCompatActivity
{
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText groceryItem;
    private EditText quantity;
    private Button saveButton;

    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHandler(this);

        byPassActivity();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                createPopupDialog();
            }
        });
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


    /***/
    public void createPopupDialog()
    {
        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup , null);
        groceryItem = (EditText) view.findViewById(R.id.groceryItem);
        quantity = (EditText) view.findViewById(R.id.groceryQty);
        saveButton = (Button) view.findViewById(R.id.saveButton);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!groceryItem.getText().toString().isEmpty() && !quantity.getText().toString().isEmpty())
                {
                    saveGroceryToDB(v);
                }
                else
                {

                }

            }
        });
    }

    private void saveGroceryToDB(View v)
    {
        Grocery grocery = new Grocery();

        String newGrocery = groceryItem.getText().toString();
        String newGroceryQuantity = quantity.getText().toString();


        grocery.setName(newGrocery);
        grocery.setQuantity(newGroceryQuantity);


        //Save to DB
        db.addGrocery(grocery);

        /*Snackbar.make(v , "Item Saved" , Snackbar.LENGTH_LONG).show();

        Log.d("Item Added ID:" , String.valueOf(db.getGroceriesCount()));*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this , ListActivity.class));
            }
        },1000);
    }


    public void byPassActivity()
    {
        /** Todo : Check If database is Empty.
         * If Not  , The Just Go to ListActivity and Show All Data */

        if(db.getGroceriesCount() > 0 )
        {
            startActivity(new Intent(MainActivity.this , ListActivity.class));
            finish();
        }
    }
}
