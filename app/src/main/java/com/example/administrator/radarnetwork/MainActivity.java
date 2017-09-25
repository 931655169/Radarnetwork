package com.example.administrator.radarnetwork;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private ArrayList<RadarView.Item> mItems=new ArrayList<RadarView.Item>();
    @BindView(R.id.radarView)
    RadarView mRadarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        for (int i=0;i<5;i++){
            RadarView.Item item=new RadarView.Item();
            item.setTitles("项目"+i);
            item.setData(10+i*10);
            mItems.add(item);
        }
        mRadarView.setItem(mItems);
        mRadarView.invalidate();
    }
}
