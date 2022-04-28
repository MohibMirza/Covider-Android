package edu.mohibmir.covider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import edu.mohibmir.covider.redis.RClass.User;
import edu.mohibmir.covider.redis.RedisClient;
import edu.mohibmir.covider.redis.RedisDatabase;

public class Settings extends AppCompatActivity implements View.OnClickListener {
    private NestedScrollView nsv;
    private EditText textFirstName;
    private EditText textLastName;
    private EditText textPassword;
    private Button b;
    private RadioButton fv;
    private RadioButton ds;
    User u;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        nsv = (NestedScrollView) findViewById(R.id.NSV);
        textFirstName = (EditText) findViewById(R.id.textInputFirstName);
        textLastName = (EditText) findViewById(R.id.textInputLastName);
        textPassword = (EditText) findViewById(R.id.textInputPassword);
        b = (Button) findViewById(R.id.Save);
        b.setOnClickListener(this);
        fv = (RadioButton) findViewById(R.id.FV);
        ds = (RadioButton) findViewById(R.id.DS);
        fv.setOnClickListener(this);
        ds.setOnClickListener(this);
        u = new User(RedisDatabase.userId);
        textFirstName.setText(u.getFirstName(), TextView.BufferType.EDITABLE);
        textLastName.setText(u.getLastName(), TextView.BufferType.EDITABLE);
        if (RedisDatabase.toggleMarkers == 1) {
            ((RadioButton)findViewById(R.id.FV)).setChecked(true);
            ((RadioButton)findViewById(R.id.DS)).setChecked(false);
        }
        else if (RedisDatabase.toggleMarkers == 0) {
            ((RadioButton)findViewById(R.id.FV)).setChecked(false);
            ((RadioButton)findViewById(R.id.DS)).setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Save:
                changeData();
                break;
            case R.id.FV:
                ((RadioButton)findViewById(R.id.DS)).setChecked(false);
                ((RadioButton)findViewById(R.id.FV)).setChecked(true);
                RedisDatabase.toggleMarkers = 1;
                break;

            case R.id.DS:
                ((RadioButton)findViewById(R.id.FV)).setChecked(false);
                ((RadioButton)findViewById(R.id.DS)).setChecked(true);
                RedisDatabase.toggleMarkers = 0;
                break;
        }
    }

    private void changeData() {
        String fname = textFirstName.getText().toString();
        if (fname.length() == 0) {
            Snackbar.make(nsv, "Invalid name", Snackbar.LENGTH_LONG).show();
            return;
        }
        String lname = textLastName.getText().toString();
        if (lname.length() == 0) {
            Snackbar.make(nsv, "Invalid name", Snackbar.LENGTH_LONG).show();
            return;
        }
        String pw = textPassword.getText().toString();

        u.setFirstName(fname);
        u.setLastName(lname);
        if (pw.length() != 0) {
            u.setPassword(pw);
        }
        Snackbar.make(nsv,"Successfully saved",Snackbar.LENGTH_SHORT).show();
        Intent it = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(it);
    }
}
