package com.grayraven.eclogintest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.grayraven.eclogintest.PoJos.Election;
import com.grayraven.eclogintest.PoJos.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String SIGN_OUT = "signout";
    private DatabaseReference mDatabase;
    private static final String TAG = "Main";
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getString(R.string.log_out));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(SIGN_OUT, true);
        startActivity(intent);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Button btnConvert = (Button) findViewById(R.id.btn_pojo_convert);
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                converIt();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mAuth= FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String displayName = mUser.getDisplayName();
        if (displayName == null) {
            displayName = "none";
        }
        String email = mUser.getEmail();
        if(email==null){
            email = "";
        }

        ((TextView)findViewById(R.id.txt_display_name)).setText(displayName);
        ((TextView)findViewById(R.id.txt_email)).setText(email);


    }

    private void converIt() {

        ArrayList<State> states = new ArrayList<State>();
        states.add(new State("AK", "Alaska", false, 5, 0, 0, 0));
        states.add(new State("AL", "Alabama", false, 6, 0, 0, 0));
        states.add(new State("DC", "DC", false, 3, 0, 0, 0));
        states.add(new State("TX", "Texas", false, 20, 0, 0, 0));

        Election election96 = new Election("Historic 1996a", "Jim says Clinton beat Dole", 1996, states);

         Gson gson = new Gson();

        String json = null;
        try {
            json = gson.toJson(election96,Election.class);
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }
        Log.d(TAG, json);


        Log.d(TAG, "current user: " + mUser.getEmail());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        String key = mDatabase.child("elections").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        String userId = "";
        String path = "users/" + mUser.getUid() + "/elections";          //http://stackoverflow.com/questions/15079163/how-can-i-use-firebase-to-securely-share-presence-data-within-a-specific-group
        childUpdates.put(path, json);
        mDatabase.updateChildren(childUpdates);

        Log.d(TAG,"DONE");
    }

}



