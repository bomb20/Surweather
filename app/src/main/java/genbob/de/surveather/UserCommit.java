package genbob.de.surveather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;


public class UserCommit extends Activity {
    private final static String TAG = "USERCOMMIT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit);
    }

    public void onClickCommit(View view){
        SeekBar air = (SeekBar) findViewById(R.id.seekBar_air);
        SeekBar noise = (SeekBar) findViewById(R.id.seekBar_noise);
        SeekBar allergie = (SeekBar) findViewById(R.id.seekBar_allergie);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("air", air.getProgress());
        intent.putExtra("noise",noise.getProgress());
        intent.putExtra("allergie",allergie.getProgress());
        Log.i(TAG,"air: " + intent.getExtras().getInt("air"));
        startActivity(intent);
    }

}
