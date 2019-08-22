package edu.ktu.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button secondActivityButton;
    private Button thirdActivityButton;
    private Context context=this;
    private Button add;
    private Button remove;
    private EditText title;
    private EditText description;
    private ListView myList;
    private ListAdapter adapter;
    private List<String> list=new ArrayList<String>();
    List<ListItem> items=new ArrayList<>();
    int in2= R.drawable.ic_accessibility_black_48dp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myList=(ListView)findViewById(R.id.listView2);
        add=(Button)findViewById(R.id.add);
        remove =(Button)findViewById(R.id.remove);
        title=(EditText) findViewById(R.id.title);
        description=(EditText) findViewById(R.id.description);

        secondActivityButton=(Button)findViewById(R.id.secondActivityButton);
        secondActivityButton.setOnClickListener(startSecondActivity);
        secondActivityButton.setOnLongClickListener(startSecondActivityLong);

        //New activity with image, title and description
        thirdActivityButton=(Button)findViewById(R.id.thirdActivityButton);
        thirdActivityButton.setOnClickListener(startThirdActivity);

        add.setOnClickListener(Add);
      //  if(items.size()!=0)
        adapter=new ListAdapter(this,items);
        myList.setAdapter(adapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                items.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        //List made on the first activity and displayed on the second
        for (int i=0;i<10;i++)
        list.add("G"+i);

    }

    public void runSecondActivity(boolean b){
        Intent intent = new Intent(context,SecondActivity.class);
        intent.putExtra("flag",b);
        intent.putStringArrayListExtra("list",(ArrayList<String>) list);
        context.startActivity(intent);
    }

    View.OnClickListener startSecondActivity=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            runSecondActivity(true);
        }
    };
    View.OnLongClickListener startSecondActivityLong=new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            runSecondActivity(false);
            return true;
        }
    };
    View.OnClickListener Add =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            items.add(new ListItem(title.getText().toString(), in2, description.getText().toString()));
            adapter.notifyDataSetChanged();
        }
    };
    View.OnClickListener startThirdActivity=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context,ThirdActivity.class);
            context.startActivity(intent);
        }
    };
}
