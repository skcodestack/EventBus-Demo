package github.com.eventbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import github.com.eventbuslib.EventBus;
import github.com.eventbuslib.Subscriber;
import github.com.eventbuslib.ThreadMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);
    }

    public void jump(View view){
        startActivity(new Intent(this,SecondActivity.class));
    }

    @Subscriber(ThreadMode.PostThread)
    public void receive(TestBean bean){
        Log.e("MainActivity","thread===>"+Thread.currentThread().getName() +"===>"+bean);

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
