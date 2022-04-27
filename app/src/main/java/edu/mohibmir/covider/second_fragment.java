package edu.mohibmir.covider;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import edu.mohibmir.covider.redis.RClass.Building;
import edu.mohibmir.covider.redis.RClass.Enums.Status;
import edu.mohibmir.covider.redis.RClass.User;
import edu.mohibmir.covider.redis.RedisDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link second_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class second_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView setClassStatus;
    private RadioButton healthyBtn,sickBtn, covidBtn, remoteBtn, inPersonBtn;
    private RadioGroup radioGroupSetClass;
    private Button submitSelfReport, submitCheckIn, submitClassSet;
    private EditText buildingNameField;
    private static String userId;


    public second_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment second_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static second_fragment newInstance(String param1, String param2) {
        second_fragment fragment = new second_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_second_fragment, container, false);

        ImageView leftIcon = view.findViewById(R.id.left_icon);
        ImageView righticon = view.findViewById(R.id.right_icon);
        TextView title = view.findViewById(R.id.toolbar_title);

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
                            Intent set = new Intent(getActivity(), Settings.class);
                            startActivity(set);
                            return true;

                        case R.id.navbarmenu_logout:
                            Intent logout = new Intent(getActivity(),Login.class);
                            startActivity(logout);
                            return true;

                        default:
                            return false;
                    }
                });
                popupMenu.show();

            }
        });

        userId = RedisDatabase.userId;

        setClassStatus = view.findViewById(R.id.setClassStatus);

        healthyBtn = view.findViewById(R.id.Healthy);
        sickBtn = view.findViewById(R.id.Sick);
        covidBtn = view.findViewById(R.id.Covid);
        remoteBtn = view.findViewById(R.id.Remote);
        inPersonBtn = view.findViewById(R.id.inPerson);

        radioGroupSetClass = view.findViewById(R.id.radioGroupSetClass);

        buildingNameField = view.findViewById(R.id.buildingName);

        submitSelfReport = view.findViewById(R.id.btnSubmit);
        submitCheckIn = view.findViewById(R.id.btnSubmit2);
        submitClassSet = view.findViewById(R.id.btnSubmit3);

        submitSelfReport.setOnClickListener(view1 -> {
            boolean healthyClicked = healthyBtn.isChecked();
            boolean sickClicked = sickBtn.isChecked();
            boolean covidClicked= covidBtn.isChecked();

            if(!healthyClicked && !sickClicked && !covidClicked) {
                Toast.makeText(getActivity(),"Please choose a status",Toast.LENGTH_SHORT).show();
                return;
            }

            if(userId.isEmpty()) {
                Toast.makeText(getActivity(),"Login Error: Please Restart App",Toast.LENGTH_SHORT).show();
            }

            User user = new User(userId);

            if(healthyClicked) {
                user.setCovidStatus(Status.healthy);
            }else if(sickClicked) {
                user.setCovidStatus(Status.symptomatic);
            }else if(covidClicked) {
                user.setCovidStatus(Status.infected);
            }
            Toast.makeText(getActivity(),"Status Updated...",Toast.LENGTH_SHORT).show();
        });

        submitCheckIn.setOnClickListener(view1 -> {
            if(userId == null) {
                Toast.makeText(getActivity(),"Login Error: Please Restart App",Toast.LENGTH_SHORT).show();
            }
            String buildingName = buildingNameField.getText().toString().toLowerCase();
            Toast.makeText(getActivity(), "Registered Visit to " + buildingName + "today.",Toast.LENGTH_SHORT).show();
            Building building = new Building(buildingName);
            building.addVisit(userId);

            User user = new User(userId);
            user.addVisit(buildingName);
        });

        User user = new User(userId);
        if(!user.getIsInstructor()) return view;

        setClassStatus.setVisibility(View.VISIBLE);

        radioGroupSetClass.setVisibility(View.VISIBLE);
        submitClassSet.setVisibility(View.VISIBLE);

        submitClassSet.setOnClickListener(view1 -> {
            boolean inPersonClicked = inPersonBtn.isChecked();
            boolean remoteClicked = remoteBtn.isChecked();

            if(!inPersonClicked && !remoteClicked) {
                Toast.makeText(getActivity(),"Please pick a choice...",Toast.LENGTH_SHORT).show();
                return;
            }

            if(inPersonClicked) {
                user.setAllClassesLive();
            }else{
                user.setAllClassesRemote();
            }

        });

        return view;
    }
}