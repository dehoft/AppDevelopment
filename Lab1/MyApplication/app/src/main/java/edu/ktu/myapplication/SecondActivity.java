package edu.ktu.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private boolean rodyti =false;
    private ListView myList;
    private ListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondactivitydesign);

        List<ListItem> items=new ArrayList<>();
        List<String> list=new ArrayList<String>();
        myList=(ListView)findViewById(R.id.listView);

        Intent intent =getIntent();
        int in= R.drawable.ic_3d_rotation_black_48dp;
        int in2= R.drawable.ic_accessibility_black_48dp;
        if (intent.getBooleanExtra("flag",true)){

            if(rodyti){
            list=intent.getStringArrayListExtra("list");
            ArrayAdapter listAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
            myList.setAdapter(listAdapter);
            }
            else {

                items.add(new ListItem("Jack", in, "Mathematics, Chemistry"));
                items.add(new ListItem("Jane",  in, "Physics, Informatics"));
                items.add(new ListItem("Bob",  in, "Mathematics, Informatics"));
                items.add(new ListItem("Clara",  in, "Geography, Chemistry"));
                items.add(new ListItem("Sam",  in, "Mathematics, Physics"));
                items.add(new ListItem("Jaack", in, "Mathematics, Chemistry"));
                items.add(new ListItem("Jaane",  in, "Physics, Informatics"));
                items.add(new ListItem("Baob",  in, "Mathematics, Informatics"));
                items.add(new ListItem("Claara",  in, "Geography, Chemistry"));
                items.add(new ListItem("Saam",  in, "Mathematics, Physics"));
                items.add(new ListItem("Jawck", in, "Mathematics, Chemistry"));
                items.add(new ListItem("Jwane",  in, "Physics, Informatics"));
                items.add(new ListItem("Bwob",  in, "Mathematics, Informatics"));
                items.add(new ListItem("Clwara",  in, "Geography, Chemistry"));
                items.add(new ListItem("Swam",  in, "Mathematics, Physics"));
            }
        }else{
            items.add(new ListItem("Mathematics", in,
                    "Mathematics is the study of topics such as quantity, structure, " +
                            " space and change."));
            items.add(new ListItem("Physics", in,
                    "Physics is the natural science that involves study of matter " +
                            " and its motion through space and time along with related" +
                            "concepts such as energy and force."));
            items.add(new ListItem("Chemistry", in,
                    "chemistry is a branch of physical science that studies the composition, " +
                            " structure, properties and change of matter."));
            items.add(new ListItem("Informatics", in,
                    "Informatics is the science of information and computer information systems."));
            items.add(new ListItem("Geography", in,
                    "Geography is a field of science devoted to the study of the lands, the features, " +
                            " the inhabitants and the phenomena of Earth."));
        }
        if(!rodyti) {
            adapter=new ListAdapter(this,items);
            myList.setAdapter(adapter);
        }
    }
}
