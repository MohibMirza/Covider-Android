package edu.mohibmir.covider;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.mohibmir.covider.databinding.ActivityNotificationBinding;
import edu.mohibmir.covider.redis.RClass.User;
import edu.mohibmir.covider.redis.RedisDatabase;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle("Notifications");

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Notifications cleared!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ListView listView = findViewById(R.id.notification_listview);
                List<String> list = new ArrayList<>();
                ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
                listView.setAdapter(arrayAdapter);

                User user = new User(RedisDatabase.userId);
                user.clearNotifications();
            }
        });

        ListView listView = findViewById(R.id.notification_listview);
        User user = new User(RedisDatabase.userId);
        List<String> list = user.getNotifications();

        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);
    }
}