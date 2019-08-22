package edu.ktu.lab2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements RequestOperator.RequestOperatorListener {

    Button sendRequestButton;
    Button button;
    TextView title;
    TextView bodyText;
    private ModelPost publication;
    private IndicatingView indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendRequestButton=(Button) findViewById(R.id.send_request);
        sendRequestButton.setOnClickListener(requestButtpnClicked);

        title=(TextView) findViewById(R.id.title);
        bodyText=(TextView) findViewById(R.id.body_text);
        button=(Button) findViewById(R.id.JsonArray);

        indicator=(IndicatingView) findViewById(R.id.generated_graphic);
    }

    View.OnClickListener requestButtpnClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendRequest();

        }
    };

    private void sendRequest()
    {
        RequestOperator ro=new RequestOperator();
        ro.setListener(this);
        ro.start();
    }

    public void updatePublication(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(publication!=null){
                    title.setText(publication.getTitle());
                    bodyText.setText(publication.getBodyText());
                    button.setText(String.valueOf(publication.getCount()));
                } else{
                    title.setText("");
                    bodyText.setText("");
                }
            }
        });
    }

    @Override
    public void success(ModelPost publication){
        this.publication=publication;
        updatePublication();
        setIndicatorStatus(IndicatingView.SUCCESS);
    }

    @Override
    public void failed(int response){
        this.publication=null;
        updatePublication();
        setIndicatorStatus(IndicatingView.FAILED);
    }

    public void setIndicatorStatus(final int status){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                indicator.setState(status);
                indicator.invalidate();
            }
        });
    }
}
