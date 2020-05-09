package com.example.myweather.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import com.example.myweather.presenter.locationView.LocationAdapter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myweather.presenter.locationView.Location;
import com.example.myweather.R;
import com.example.myweather.util.JsonObject;
import com.example.myweather.util.ThreadManager;

import java.util.ArrayList;
import java.util.List;

import static com.example.myweather.util.Load.makeStatusBarTransparent;

public class LocationResultActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Location> mLocList = new ArrayList<>();

    private EditText searchEdit;

    final private int UPDATE_TEXT = 1;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == UPDATE_TEXT) {
                RecyclerView locRec = findViewById(R.id.loc_rey);
                LinearLayoutManager manager = new LinearLayoutManager(LocationResultActivity.this);
                manager.setOrientation(LinearLayoutManager.VERTICAL);
                locRec.setLayoutManager(manager);
                LocationAdapter adapter = new LocationAdapter(mLocList);
                locRec.setAdapter(adapter);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeStatusBarTransparent(LocationResultActivity.this);
        setContentView(R.layout.activity_location_result);
        getWindow().getDecorView().setBackgroundResource(R.drawable.gray_backg);
        searchEdit = findViewById(R.id.search_edit_text);
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search();
                }
                return true;
            }
        });
        findViewById(R.id.search_back_button).setOnClickListener(this);
        findViewById(R.id.send_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                search();
                break;
            case R.id.search_back_button:
                Intent intent = new Intent(LocationResultActivity.this, WeatherActivity.class);
                startActivity(intent);
                break;
        }
    }

    void search(){
        mLocList.clear();
        String loc = searchEdit.getText().toString();
        JsonObject jsonObject = new JsonObject(mLocList,new ThreadManager().getExecutorService());
        jsonObject.requestWeather("find", loc);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Message message = new Message();
                    message.what = UPDATE_TEXT;
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}