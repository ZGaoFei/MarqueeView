package com.zgf.marqueview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TEXT = "恭喜张三、李四、王二、麻子等10万人，平均分得10万元奖金，欢迎前来领奖";
    private MarqueeView marqueeView;
    private MarqueeLayout marqueeLayout;
    private MarqueeLayout marqueeLayout2;
    private TextView innerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.tv_marquee);
        textView.setFocusable(true);

        Button button = findViewById(R.id.bt_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marqueeView.run();

                marqueeLayout.run();

                marqueeLayout2.run();
            }
        });

        marqueeView = findViewById(R.id.marquee_view);
        init();

        marqueeLayout = findViewById(R.id.marquee_layout);
        initMarqueeLayout();

        marqueeLayout2 = findViewById(R.id.marquee_layout2);
        innerTextView = findViewById(R.id.text_view);
        initMarqueeLayout2();
    }

    private void init() {
        marqueeView.setText(TEXT);
        marqueeView.setMode(MarqueeView.MODE_FOREVER);
        marqueeView.run();
        marqueeView.setListener(new ProgressListenerAdapter() {
            @Override
            public void onAnimationEnd() {
                super.onAnimationEnd();

            }
        });
    }

    private void initMarqueeLayout() {
        marqueeLayout.setText(TEXT);
        marqueeLayout.setMode(MarqueeLayout.MODE_FOREVER);
        marqueeLayout.run();
        marqueeLayout.setListener(new ProgressListenerAdapter() {
            @Override
            public void onAnimationEnd() {
                super.onAnimationEnd();

            }
        });
    }

    private void initMarqueeLayout2() {
        innerTextView.setText(TEXT);
        marqueeLayout2.setMode(MarqueeLayout.MODE_FOREVER);
        marqueeLayout2.run();
        marqueeLayout2.setListener(new ProgressListenerAdapter() {
            @Override
            public void onAnimationEnd() {
                super.onAnimationEnd();

            }
        });
    }
}
