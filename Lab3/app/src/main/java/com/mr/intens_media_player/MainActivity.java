package com.mr.intens_media_player;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String SOUND_ID = "sound_id";
    public static final int BUTTON_REQUEST = 1;
    private int current_sound = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageButton imageButton = (ImageButton)findViewById(R.id.face_button);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int random_color = 0xff000000;
                for (int i = 0; i<3; i++){
                    int component_color = (int)(255 * Math.random());
                    component_color <<= (8*i);
                    random_color |= component_color;
                }
                imageButton.setColorFilter(random_color);
            }
        });
        imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent soundPick = new Intent(getApplicationContext(),SecondActivity.class);
                soundPick.putExtra(SOUND_ID,current_sound);
                startActivityForResult(soundPick,BUTTON_REQUEST);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if(requestCode == BUTTON_REQUEST)
            {
                current_sound = data.getIntExtra(SOUND_ID,0);
            }
        }
        else if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(),getText(R.string.back_message),Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
