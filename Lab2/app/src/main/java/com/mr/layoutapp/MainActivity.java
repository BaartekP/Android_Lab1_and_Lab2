package com.mr.layoutapp;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setButtonsClickListener();
        setSpinnerItemSelectedListener();
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

    public void myClickHandler(View view) {
        TextView txtV = (TextView) findViewById(R.id.text_view);
        switch (view.getId()) {
            case R.id.button:
                txtV.setTextColor(Color.RED);
                break;
            case R.id.button2:
                txtV.setTextColor(Color.GREEN);
                break;
            case R.id.button3:
                txtV.setTextColor(Color.BLUE);
                break;
            default:
        }
    }

    public void setButtonsClickListener() {
        Button buttonRed = (Button) findViewById(R.id.button);
        Button buttonGreen = (Button) findViewById(R.id.button2);
        Button buttonBlue = (Button) findViewById(R.id.button3);

        final View.OnClickListener myClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myClickHandler(view);
            }
        };
        if (buttonRed != null)
            buttonRed.setOnClickListener(myClickListener);
        if (buttonGreen != null)
            buttonGreen.setOnClickListener(myClickListener);
        if (buttonBlue != null)
            buttonBlue.setOnClickListener(myClickListener);
    }

    public void setSpinnerItemSelectedListener() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_v);
        if (spinner != null) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ImageView logoView = (ImageView) findViewById(R.id.image_v);
                    if (logoView != null) {
                        Drawable drawable;

                        switch (i) {
                            case 0:
                                drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pp_logo, null);
                                break;
                            case 1:
                                drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.et_logo, null);
                                break;
                            case 2:
                                drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.kr_logo, null);
                                break;
                            default:
                                drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.et_logo, null);
                        }
                        logoView.setImageDrawable(drawable);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }
    }

}



