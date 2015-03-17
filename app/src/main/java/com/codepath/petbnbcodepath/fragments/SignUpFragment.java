package com.codepath.petbnbcodepath.fragments;

import android.app.Activity;
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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by vibhalaljani on 3/14/15.
 */
public class SignUpFragment extends Fragment {
    private Button btnSignUp;
    private EditText etEmail;
    private EditText etPwd;
    private EditText etFirstName;
    private EditText etLastName;
    private TextView tvLogin;

    private OnLoginLinkClickedListener listener;

    private static final String TAG = "SIGNUPFRAGMENT";

    public interface OnLoginLinkClickedListener {
        public void onLogin();
        public void onFinish();
        public void onError(String title, String message);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnLoginLinkClickedListener) {
            listener = (OnLoginLinkClickedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        setupViews(view);

        return view;
    }

    private void setupViews(View view) {
        btnSignUp = (Button) view.findViewById(R.id.btnSignUp);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPwd = (EditText) view.findViewById(R.id.etPwd);
        etFirstName = (EditText) view.findViewById(R.id.etFirstName);
        etLastName = (EditText) view.findViewById(R.id.etLastName);
        tvLogin = (TextView) view.findViewById(R.id.tvLogin);

        setupViewListeners();
    }

    private void setupViewListeners() {
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLogin();
            }
        });
        etLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (etEmail.getText().toString().matches(Constants.emailRegex) &&
                            etFirstName.getText().toString().length() > 0 &&
                            etPwd.getText().toString().length() > 7) {
                        btnSignUp.setEnabled(true);
                        btnSignUp.setAlpha(Constants.btnEnabledAlpha);
                    }
                }
                if (s.length() <= 0) {
                    btnSignUp.setEnabled(false);
                    btnSignUp.setAlpha(Constants.btnDisabledAlpha);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (etEmail.getText().toString().matches(Constants.emailRegex) &&
                        etLastName.getText().toString().length() > 0 &&
                        etPwd.getText().toString().length() > 7) {
                        btnSignUp.setEnabled(true);
                        btnSignUp.setAlpha(Constants.btnEnabledAlpha);
                    }
                }
                if (s.length() <= 0) {
                    btnSignUp.setEnabled(false);
                    btnSignUp.setAlpha(Constants.btnDisabledAlpha);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (etEmail.getText().toString().matches(Constants.emailRegex) &&
                            etLastName.getText().toString().length() > 0 &&
                            etPwd.getText().toString().length() > 7) {
                        btnSignUp.setEnabled(true);
                        btnSignUp.setAlpha(Constants.btnEnabledAlpha);
                    }
                }
                if (s.length() <= 0 || !(etEmail.getText().toString().matches(Constants.emailRegex))) {
                    btnSignUp.setEnabled(false);
                    btnSignUp.setAlpha(Constants.btnDisabledAlpha);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 7) {
                    if (etEmail.getText().toString().matches(Constants.emailRegex) &&
                            etLastName.getText().toString().length() > 0) {
                        btnSignUp.setEnabled(true);
                        btnSignUp.setAlpha(Constants.btnEnabledAlpha);
                    }
                }
                if (s.length() <= 7) {
                    btnSignUp.setEnabled(false);
                    btnSignUp.setAlpha(Constants.btnDisabledAlpha);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEmail.clearFocus();
                etFirstName.clearFocus();
                etLastName.clearFocus();
                etPwd.clearFocus();
                onSignUp();
            }
        });
    }

    public void onSignUp() {
        ParseUser user = new ParseUser();
        user.setUsername(etEmail.getText().toString());
        user.setPassword(etPwd.getText().toString());
        user.setEmail(etEmail.getText().toString());

        // other fields can be set just like with ParseObject
        user.put(Constants.firstNameKey, etFirstName.getText().toString());
        user.put(Constants.lastNameKey, etLastName.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getActivity(),
                            "You are now signed up " + ParseUser.getCurrentUser().toString(),
                            Toast.LENGTH_SHORT).show();
                    listener.onFinish();
                } else {
                    String usernameTakenError = "username " + etEmail.getText().toString() +
                                                " already taken";
                    Log.e(TAG, "Error: " + e.getMessage());
                    if(e.getMessage().equals(usernameTakenError)) {
                        listener.onError(getResources().getString(R.string.signup_error),
                                getResources().getString(R.string.user_exists_err_msg));
                    } else {
                        listener.onError(getString(R.string.signup_error),
                                e.getMessage());
                    }
                }
            }
        });
    }

    // Creation lifecycle event
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
