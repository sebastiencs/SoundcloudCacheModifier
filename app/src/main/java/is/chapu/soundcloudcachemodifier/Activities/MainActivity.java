package is.chapu.soundcloudcachemodifier.Activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import is.chapu.soundcloudcachemodifier.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int DEFAULT_VALUE = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView)findViewById(R.id.textView);

        final SharedPreferences pref = getSharedPreferences("pref_sd", MODE_WORLD_READABLE);

        if (!pref.contains("size_cache")) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("size_cache", DEFAULT_VALUE);
            editor.commit();
        }

        textView.setText(Integer.toString(pref.getInt("size_cache", DEFAULT_VALUE)));

        DiscreteSeekBar sb = (DiscreteSeekBar)findViewById(R.id.discrete_bar);
        sb.setProgress(pref.getInt("size_cache", DEFAULT_VALUE));
        sb.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                Log.d(TAG, "Tracking stop: " + seekBar.getProgress());
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("size_cache", seekBar.getProgress());
                editor.commit();
                textView.setText(Integer.toString(pref.getInt("size_cache", DEFAULT_VALUE)));
            }
        });
    }
}
