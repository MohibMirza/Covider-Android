package edu.mohibmir.covider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.redisson.api.RLiveObjectService;
import org.redisson.client.RedisClientConfig;

import edu.mohibmir.covider.redis.RClass.Building;
import edu.mohibmir.covider.redis.RedisClient;

import static edu.mohibmir.covider.redis.RedisDatabase.*;

public class MainActivity extends AppCompatActivity {

    private static String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // This starts the redis client
        // This code should only be run once when the app starts. If
        // you need to access redis use RedisClient.getInstance()
    }
}