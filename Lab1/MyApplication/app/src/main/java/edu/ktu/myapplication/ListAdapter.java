package edu.ktu.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ListAdapter extends ArrayAdapter<ListItem> {
    private List<ListItem> obejcts=new ArrayList<>();

    public ListAdapter (Context context, List<ListItem> objects){
        super(context,R.layout.design,objects);
        this.obejcts=objects;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View v=convertView;

        if(v==null){
            LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.design,null);
        }

        TextView title=(TextView) v.findViewById(R.id.title);
        TextView description=(TextView)v.findViewById(R.id.description);
        ImageView image =(ImageView)v.findViewById(R.id.image);

        ListItem item=getItem(position);

        title.setText(item.getTitle());
        description.setText(item.getDescription());
        image.setImageResource(item.getImageID());

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obejcts.remove(position);
                notifyDataSetChanged();
            }
        });
        return v;
    }
}
