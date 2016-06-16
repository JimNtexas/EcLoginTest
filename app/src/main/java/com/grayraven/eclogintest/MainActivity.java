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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.grayraven.eclogintest.PoJos.Election;
import com.grayraven.eclogintest.PoJos.State;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    public static final String SIGN_OUT = "signout";
    private DatabaseReference mDatabase;
    private static final String TAG = "Main";
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ValueEventListener mListener;

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

        Button btnRead = (Button)findViewById(R.id.btn_read_data);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        String displayName = mUser.getUid();
        if (displayName == null) {
            displayName = "none";
        }
        String email = mUser.getEmail();
        if(email==null){
            email = "";
        }

        ((TextView)findViewById(R.id.txt_display_name)).setText(displayName);
        ((TextView)findViewById(R.id.txt_email)).setText(email);

         /* ---- Firebase data changes ----*/
         mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Value event change cnt: " + dataSnapshot.getChildrenCount());
                DataSnapshot elections = dataSnapshot.child("users/" + mUser.getUid() + "/elections");
                Log.d(TAG, "election count: " + elections.getChildrenCount());
                for(DataSnapshot  el: elections.getChildren()){
                      Log.d(TAG, "key  :" +el.getKey());
                      Election e = getElection(el.getValue().toString());
                      Log.d(TAG, "Election:" + e.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDatabase.addValueEventListener(mListener);


    }

    private void converIt() {

        ArrayList<State> states = new ArrayList<State>();
        states.add(new State("AK", "Alaska", false, 5, 0, 0, 0));
        states.add(new State("AL", "Alabama", false, 6, 0, 0, 0));
        states.add(new State("DC", "DC", false, 3, 0, 0, 0));
        states.add(new State("TX", "Texas", false, 20, 0, 0, 0));

        String title = mUser.getEmail() + "1996";
        Election election96 = new Election(title, "first time through", 1996, states);
        title = mUser.getEmail() + "2000";
        Election election2000 = new Election(title, "second time through", 2000, states);

        Log.d(TAG, "current user: " + mUser.getEmail());
        String path = "users/" + mUser.getUid() + "/elections";
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(path + "/1996").setValue(getJson(election96));
        mDatabase.child(path + "/2000").setValue(getJson(election2000));

        Log.d(TAG,"DONE");
    }

    private String getJson(Election election96) {
        String json = null;
        Gson gson = new Gson();
        try {
            json = gson.toJson(election96,Election.class);
        } catch (Exception e) {
            Log.e("TAG", "Json error: " + e.getMessage());
            return null;
        }
        return json;
    }

    private Election getElection(String json) {
        Gson gson = new Gson();
        Election e = gson.fromJson(json, Election.class);
        return e;
    }

}



