package com.codepath.petbnbcodepath.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.petbnbcodepath.R;
import com.codepath.petbnbcodepath.helpers.Constants;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by vibhalaljani on 3/14/15.
 */
public class LoginFragment extends Fragment {

    private static final String TAG = "LOGINFRAGMENT";

    private Button btnLogin;
    private EditText etEmail;
    private EditText etPwd;
    private TextView tvSignUp;

    private OnSignUpLinkClickedListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnSignUpLinkClickedListener) {
            listener = (OnSignUpLinkClickedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    public interface OnSignUpLinkClickedListener {
        public void onSignUp();
        public void onFinish();
        public void onError(String title, String message);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        setupViews(view);

        return view;
    }


    // Creation lifecycle event
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupViews(View view) {
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPwd = (EditText) view.findViewById(R.id.etPwd);
        tvSignUp = (TextView) view.findViewById(R.id.tvSignUp);

        setupViewListeners();
    }

    private void setupViewListeners() {
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSignUp();
            }
        });
        etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 7) {
                    if (etEmail.getText().toString().matches(Constants.emailRegex)) {
                        btnLogin.setEnabled(true);
                        btnLogin.setAlpha(Constants.btnEnabledAlpha);
                    }
                }
                if (s.length() < 7) {
                    btnLogin.setEnabled(false);
                    btnLogin.setAlpha(Constants.btnDisabledAlpha);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
        });

    }

    public void onLogin() {
        String email = etEmail.getText().toString();
        if (! email.matches(Constants.emailRegex)) {
            Toast.makeText(getActivity(),
                    getResources().getString(R.string.checkEmail),
                    Toast.LENGTH_SHORT).show();
        } else {
            final String invalidCredError = "invalid login credentials";
            ParseUser.logInInBackground(email,
                    etPwd.getText().toString(),
                    new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                // This should return back to the activity that started it
                                Toast.makeText(getActivity(),
                                        "You are now logged in",
                                        Toast.LENGTH_SHORT).show();
                                listener.onFinish();
                            } else {
                                Log.e(TAG, "Error: " + e.getMessage());
                                if(e.getMessage().equals(invalidCredError)) {
                                    listener.onError(getResources().getString(R.string.login_error),
                                               getResources().getString(R.string.invalid_cred_msg));
                                } else {
                                    listener.onError(getString(R.string.login_error),
                                                     e.getMessage());
                                }
                            }
                        }
                    });
        }

    }
}
