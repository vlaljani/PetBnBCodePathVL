package com.codepath.petbnbcodepath.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.petbnbcodepath.R;
import com.codepath.petbnbcodepath.fragments.LoginFragment;
import com.codepath.petbnbcodepath.fragments.LoginSignUpFragment;
import com.codepath.petbnbcodepath.fragments.SignUpFragment;
import com.codepath.petbnbcodepath.helpers.CustomDialogBuilder;


public class LoginSignupActivity extends ActionBarActivity
                                 implements LoginSignUpFragment.OnButtonClickedListener,
                                            LoginFragment.OnSignUpLinkClickedListener,
                                            SignUpFragment.OnLoginLinkClickedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);
        setupViews();
    }

    public void onLogin() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the container with the new fragment
        ft.add(R.id.frag_login_signup, new LoginFragment());
        // Execute the changes specified
        ft.commit();
    }

    public void onSignUp() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the container with the new fragment
        ft.add(R.id.frag_login_signup, new SignUpFragment());
        // Execute the changes specified
        ft.commit();
    }

    public void onFinish() {
        finish();
    }

    public void onError(String title, String message) {

        CustomDialogBuilder alertDialogBuilder = new CustomDialogBuilder(LoginSignupActivity.this);

        // set title
        alertDialogBuilder.setTitle(title);

        alertDialogBuilder.setTitleColor();

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                dialog.dismiss();
                            }
                        });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void setupViews() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the container with the new fragment
        ft.add(R.id.frag_login_signup, new LoginSignUpFragment());
        ft.addToBackStack("login_signup_frag");
        // Execute the changes specified
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
