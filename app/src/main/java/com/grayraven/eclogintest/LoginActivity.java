package com.grayraven.eclogintest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login";
    private static final int REQUEST_READ_CONTACTS = 0;
    private FirebaseAuth mAuth;
    private final int mMinPasswordLength = 6;



    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
  //  private View mProgressView;
 //   private View mLoginFormView;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        // Set up the login form
        // todo: implement auto complete
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    gotoMainActivity();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }


        };

        Button resetPw = (Button)findViewById(R.id.pw_reset_btn);
        resetPw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth != null) {
                   requestPwReset();
                }
            }
        });

        // new email user
        Button newUser = (Button)findViewById(R.id.new_user_button);
        newUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewUser();
            }
        });
    } // end onCreate

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // firebase password reset
    private void requestPwReset() {
        String email = mEmailView.getText().toString();
        Log.d(TAG, "sending pw reset request for: " + email);
        Task<Void> task = mAuth.sendPasswordResetEmail(email);
        Log.d("TAG", "requestPwReset result: " + (task.isSuccessful() == true) );
        ShowDismissableSnackbar(getString(R.string.pw_reset_snack_msg), true);
    }


    void ShowDismissableSnackbar( String msg, boolean indef) {
        final Snackbar bar = Snackbar.make(findViewById(R.id.login_activity), msg, indef ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_LONG);
        bar.setAction(R.string.dismiss, new OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.dismiss();
            }
        });
        bar.show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        EventBus.getDefault().register(this);
    }




    // create new firebase user
    //TODO:  implement email verification -  http://andreasmcdermott.com/web/2014/02/05/Email-verification-with-Firebase/
    //Note:  Feature is pending from Firebase: https://console.firebase.google.com/project/project-57952108922096486/authentication/emails
    private void createNewUser() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        if(!isEmailValid(email)) {
            ShowDismissableSnackbar(getString(R.string.new_user_enter_email), false);
            mEmailView.requestFocus();
            return;
        }
        if(!isPasswordValid(password)){
            String errorFormat = this.getString(R.string.error_invalid_password_fmt);
            String errorMsg = String.format(errorFormat, mMinPasswordLength);
            ShowDismissableSnackbar(errorMsg, false);
            return;

        }

        if(mAuth != null) {
            mAuth.signOut();
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }



    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            String errorFormat = this.getString(R.string.error_invalid_password_fmt);
            String errorMsg = String.format(errorFormat, mMinPasswordLength);
            mPasswordView.setError(errorMsg);
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Firebase email login

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();  //todo: handle failure
                            }
                        }
                    });



          /*  // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);*/
        }
    }

    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > mMinPasswordLength;
    }

    /*// This method will be called when a MessageEvent is posted
    @Subscribe
    public void onMessageEvent(MessageEvent event){
       if(event.message == MessageEvent.LOG_OUT_MSG) {
           Log.d(TAG, "Logout message received");
           if(mAuth != null) {
               mAuth.signOut();
               mAuth = null;
           }
       }
    }*/






}

