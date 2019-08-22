package edu.ktu.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class ThirdActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thirdactivitydesign);

        TextView title=(TextView) findViewById(R.id.title);
        TextView description=(TextView)findViewById(R.id.description);
        ImageView image =(ImageView) findViewById(R.id.image);

        image.setImageResource(R.drawable.ic_accessibility_black_48dp);
        title.setText("Title");
        description.setText("Description");
    }
}
