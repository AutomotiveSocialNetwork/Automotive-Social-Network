package com.example.trackyourlocation;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

//a class for database activity

public class DataBase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("places");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle MapsActivity=getIntent().getExtras();
        if(MapsActivity==null)
        {
            return;
        }

        TextView t=(TextView)findViewById(R.id.mytextView);
        String message=MapsActivity.getString("database");
        t.setText(message);




    }
    public void delete (View view)
    {
        EditText t = (EditText) findViewById(R.id.etLocationEntry);
        String ss =t.getText().toString();
        Intent i =new Intent(this,MapsActivityDemo1.class);
        i.putExtra("deleteMessage",ss);
        startActivity(i);
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RelativeLayout mylayout = (RelativeLayout) findViewById(R.id.mylayout);
        switch (item.getItemId()) {
            case R.id.menu_database:
                if (item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);

                Intent i = new Intent(this,DataBase.class);
                startActivity(i);


                return true;

            case R.id.menu_map:
                if (item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                Intent ii = new Intent(this,MapsActivityDemo1.class);
                startActivity(ii);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
*/
}
