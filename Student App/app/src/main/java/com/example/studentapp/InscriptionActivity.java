package com.example.studentapp;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.studentapp.Models.User;
import com.example.studentapp.Utils.Executer;
import com.example.studentapp.Utils.Mqtt;
import com.example.studentapp.Utils.UserLocalStore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lukedeighton.wheelview.WheelView;
import com.lukedeighton.wheelview.adapter.WheelAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

public class InscriptionActivity extends AppCompatActivity {

//    private WheelView wheel;
    private String TAG = "InscriptionActivity";
    private UserLocalStore userStored;
    private Gson gson = new Gson();
    private LinearLayout add_btn;
    private LinearLayout queue_list;
    private Mqtt mqtt;
    private final String publishTopic = "androidRequestQueueList";
    private final String subcribeTopic = "androidRequestQueueResponse";

//    int size = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        Mqtt.initialize(this);

//        final ShapeDrawable[] shapeDrawables = new ShapeDrawable[size];
//        for(int i = 0 ; i < size ; ++i) {
//            shapeDrawables[i] = new ShapeDrawable(new OvalShape());
//            int color = ContextCompat.getColor(this, R.color.colorLight);
//            shapeDrawables[i].getPaint().setColor(color);
//        }
//
//        this.wheel = (WheelView)this.findViewById(R.id.wheel_view);
//        this.wheel.setVisibility(View.GONE);
//        wheel.setWheelItemCount(size);
//        wheel.setAdapter(new WheelAdapter() {
//            @Override
//            public Drawable getDrawable(int position) {
//                return shapeDrawables[position];
//            }
//
//            @Override
//            public int getCount() {
//                return size;
//            }
//        });

        this.queue_list = (LinearLayout)this.findViewById(R.id.queue_list);

        Mqtt.mqtt.publish(publishTopic, "");
        Mqtt.mqtt.subscribe(subcribeTopic, new Executer() {
            @Override
            public void execute(String messageFromMqtt) {
                Log.d(TAG, messageFromMqtt);
                JsonArray jsonArray = gson.fromJson(messageFromMqtt, JsonArray.class);
                Log.d(TAG, jsonArray.toString());
                for(JsonElement queue : jsonArray) {
                    JsonObject obj = queue.getAsJsonObject();
                    int id = gson.fromJson(obj.get("id"), Integer.class);
                    String name = gson.fromJson(obj.get("name"), String.class);

                    Button item = new Button(getApplicationContext());
                    item.setText(name);
                    item.setTag(id);
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Mqtt.mqtt.disconnect();
                            finish();
                        }
                    });
                    queue_list.addView(item);
                }
            }
        });

        this.add_btn = (LinearLayout)this.findViewById(R.id.add_btn);
        this.add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
