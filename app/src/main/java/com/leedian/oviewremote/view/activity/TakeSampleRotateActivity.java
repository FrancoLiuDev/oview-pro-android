package com.leedian.oviewremote.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.leedian.oviewremote.R;
import com.leedian.oviewremote.utils.viewUtils.ActionBarTop;

public class TakeSampleRotateActivity extends AppCompatActivity implements View.OnClickListener, ActionBarTop.ActionButtonEvent {

    protected ActionBarTop actionBar;
    float degree = 0;
    @Bind(R.id.seekbar_degree)
    AppCompatSeekBar seekBarDegree;

    @Bind(R.id.textbutton_rotate_confirm)
    TextView textview_confirm;

    @Bind(R.id.imageView)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = new ActionBarTop.Builder(this, R.layout.actionbar_menu_bar)
                .setEventListener(this).setDebugMode(false)
                .setBackgroundColor(Color.parseColor("#00000000")).build();

        setContentView(R.layout.activity_take_sample_rotate);

        ButterKnife.bind(this);
        textview_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(1);

                Intent resultIntent = new Intent();

                resultIntent.putExtra("degree", (int) degree);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        seekBarDegree.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                degree = progress * (360f / 100f);
                degree = progress * (360f / 100f);

                Matrix matrix = new Matrix();
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                matrix.postRotate(-degree, imageView.getWidth() / 2, imageView.getHeight() / 2);
                imageView.setImageMatrix(matrix);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onActionButtonClicked(View view) {

    }
}
