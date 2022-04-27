package edu.mohibmir.covider;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import edu.mohibmir.covider.redis.RClass.Building;
import edu.mohibmir.covider.redis.RedisDatabase;

public class BuildingList extends AppCompatActivity {
    ListView l1;
    ListView l2;
    List<String> names;
    List<String> risks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.building_list);

        names = new ArrayList<>();
        risks = new ArrayList<>();
        names.add("Building Name");
        risks.add("Risk Score");
        for (String b : RedisDatabase.buildingNames) {
            Building temp = RedisDatabase.getOrCreateBuilding(b);
            names.add(b);
            risks.add(Double.toString(temp.getRiskScore()));
        }



        l1 = findViewById(R.id.list1);
        ArrayAdapter<String> arr1 = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                names);
        l1.setAdapter(arr1);
        l2 = findViewById(R.id.list2);
        ArrayAdapter<String> arr2 = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                risks);
        l2.setAdapter(arr2);
    }
}
