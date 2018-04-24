package com.openxu.bmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showMap(View view){
        startActivity(new Intent(this, ShowMapActivity.class));
    }
    public void offlineMap(View view){
        startActivity(new Intent(this, OffLineMapActivity.class));
    }
    public void overLay(View view){
        startActivity(new Intent(this, GroundOverlayActivity.class));
    }
    public void overLay1(View view){
        startActivity(new Intent(this, GroundOverlayActivity1.class));
    }
    public void overLay2(View view){
        startActivity(new Intent(this, GroundOverlayActivity2.class));
    }

}
