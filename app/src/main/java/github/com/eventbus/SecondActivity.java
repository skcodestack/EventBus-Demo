package github.com.eventbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import github.com.eventbuslib.EventBus;


public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public void send(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new TestBean("发送的消息"));
            }
        }).start();


    }
}
