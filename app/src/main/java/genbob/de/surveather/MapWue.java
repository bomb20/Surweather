package genbob.de.surveather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MapWue extends AppCompatActivity {
    //TODO: implement OSM
    private static final String TAG = "MAP_VALUE";
    private float mx;
    private float my;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_wue);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView_map);
        if(getIntent().getExtras() != null){
            Log.d(TAG,"DATA: " + getIntent().getExtras().getInt("map"));
            imageView.setImageResource( getIntent().getExtras().getInt("map"));
        }


        // set maximum scroll amount (based on center of image)
        imageView.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View arg0, MotionEvent event) {



                float curX, curY;

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mx = event.getX();
                        my = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        curX = event.getX();
                        curY = event.getY();
                        imageView.scrollBy((int) (mx - curX), (int) (my - curY));
                        mx = curX;
                        my = curY;
                        break;
                    case MotionEvent.ACTION_UP:
                        curX = event.getX();
                        curY = event.getY();
                        imageView.scrollBy((int) (mx - curX), (int) (my - curY));
                        break;
                }

                return true;
            }

        });
    }
}
