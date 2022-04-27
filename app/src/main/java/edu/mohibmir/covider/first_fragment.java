package edu.mohibmir.covider;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import java.lang.Object.*;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import java.lang.Object.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

import edu.mohibmir.covider.redis.RClass.Building;
import edu.mohibmir.covider.redis.RClass.User;
import edu.mohibmir.covider.redis.RedisDatabase;

public class first_fragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first_fragment, container, false);

        // toolbar tingz
        ImageView leftIcon = rootView.findViewById(R.id.left_icon);
        ImageView righticon = rootView.findViewById(R.id.right_icon);
        TextView title = rootView.findViewById(R.id.toolbar_title);

        righticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("STATE", "TOOLBAR CLICKED");
                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.getMenuInflater().inflate(R.menu.navbarmenu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch(menuItem.getItemId()){
                        case R.id.navbarmenu_buildings:
                            Log.d("STATE", "NAV BAR BUILDINGS LIST CLICKED");
                            Intent in = new Intent(getActivity(), BuildingList.class);
                            startActivity(in);
                            return true;
                        case R.id.navbarmenu_notifications:
                            Log.d("STATE", "NAV BAR BUILDINGS NOTIFICATIONS CLICKED");
                            Intent myIntent = new Intent(getActivity(), NotificationActivity.class);
                            startActivity(myIntent);


                            return true;
                        case R.id.navbarmenu_settings:
                            Log.d("STATE", "NAV BAR MENU SETTINGS CLICKED");
                            return true;
                        default:
                            return false;
                    }
                });
                popupMenu.show();

            }
        });

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.getUiSettings().setAllGesturesEnabled(false);
                //get latlong for corners for specified place
                LatLng one = new LatLng(34.023595, -118.289880);
                LatLng two = new LatLng(34.018840, -118.282146);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                //add them to builder
                builder.include(one);
                builder.include(two);

                LatLngBounds bounds = builder.build();

                //get width and height to current display screen
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;

                // 20% padding
                int padding = (int) (width * 0.20);

                //set latlong bounds
                mMap.setLatLngBoundsForCameraTarget(bounds);

                //move camera to fill the bound to screen
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));

                //set zoom to level to current so that you won't be able to zoom out viz. move outside bounds
                mMap.setMinZoomPreference(mMap.getCameraPosition().zoom);

                for (String buildingName : RedisDatabase.buildingNames) {

                    Building building = new Building(buildingName);
                    User user = new User(RedisDatabase.userId);

                    String text = building.getName() + " Instructions: " + building.getInstructions()
                            + ". Score: "  + building.getRiskScore();


                    LatLng latLng = new LatLng(building.getLatitude(), building.getLongitude());

                    int visitedCount = user.getBuildingVisitCount(buildingName);

                    float color = 0.0f;

                    if(visitedCount < 15) {
                        color = BitmapDescriptorFactory.HUE_RED;
                    }else if(visitedCount < 55) {
                        color = BitmapDescriptorFactory.HUE_ORANGE;
                    }else {
                        color = BitmapDescriptorFactory.HUE_GREEN;
                    }

                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(text)
                            .icon(BitmapDescriptorFactory.defaultMarker(color)));


                }

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(final LatLng latLng) {


                    }
                });
            }
        });



        return rootView;
    }
}
