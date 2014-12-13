package edu.uci.ics.androidsensors;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.content.Intent;

public class MainActivity extends Activity {

    ImageButton imageButton1, imageButton2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addListenerOnButton();

    }

    public void addListenerOnButton() {

        imageButton1 = (ImageButton) findViewById(R.id.imageButton1); //snare
        imageButton2 = (ImageButton) findViewById(R.id.imageButton2); //crash cymbal

        imageButton1.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View arg0){
                Intent intent = new Intent(getApplicationContext(), SnareActivity.class);
                if(!imageButton1.isSelected())
                {
                    imageButton1.setSelected(true);
                    imageButton2.setSelected(false);
                }
                else
                {
                    imageButton1.setSelected(false);
                }
                startActivity(intent);
            }
        });

        imageButton2.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View arg0){
                Intent intent = new Intent(getApplicationContext(), CrashCymbalActivity.class);
                if(!imageButton2.isSelected())
                {
                    imageButton1.setSelected(false);
                    imageButton2.setSelected(true);
                }
                else
                {
                    imageButton2.setSelected(false);
                }
                startActivity(intent);
            }
        });
    }
}
