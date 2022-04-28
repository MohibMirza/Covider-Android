package edu.mohibmir.covider;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.mohibmir.covider.redis.RClass.Class;
import edu.mohibmir.covider.redis.RClass.User;
import edu.mohibmir.covider.redis.RedisDatabase;

public class ClassHealthHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_health_history);

        ListView listView = findViewById(R.id.listview);

        ArrayList arrayList = new ArrayList<>();
        User user = new User(RedisDatabase.userId);
        Class class_ = new Class(user.getClass1());

        List<String> notifications;
        notifications = class_.getNotifications();

        for (int i = 0; i<notifications.size(); i++)
        {
            arrayList.add(notifications.get(i));
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        }
    }