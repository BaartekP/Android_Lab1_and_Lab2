package com.mr.intens_media_player;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final String SOUND_ID = "sound_id";
    public static final int BUTTON_REQUEST = 1;
    private int current_sound = 0;

    private MediaPlayer backgroundPlayer;
    private MediaPlayer buttonPlayer;
    static public Uri[] sounds;

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

        sounds = new Uri[4];
        sounds[0] = Uri.parse("android.resource://" + getPackageName() + "/" +
                R.raw.ringd);
        sounds[1] = Uri.parse("android.resource://" + getPackageName() + "/" +
                R.raw.ring01);
        sounds[2] = Uri.parse("android.resource://" + getPackageName() + "/" +
                R.raw.ring02);
        sounds[3] = Uri.parse("android.resource://" + getPackageName() + "/" +
                R.raw.ring03);

        buttonPlayer = new MediaPlayer();
        buttonPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        buttonPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                backgroundPlayer.pause();
                mediaPlayer.start();
            }
        });

        buttonPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                backgroundPlayer.start();
            }
        });

        buttonPlayer.reset();
        try {
            buttonPlayer.setDataSource(getApplicationContext(),sounds[current_sound]);
        } catch (IOException e){
            e.printStackTrace();
        }

        buttonPlayer.prepareAsync();

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



    @Override
    protected void onPause() {
        super.onPause();
        backgroundPlayer.pause();
        buttonPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundPlayer = MediaPlayer.create(this, R.raw.mario);
        backgroundPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        backgroundPlayer.release();
    }


}
