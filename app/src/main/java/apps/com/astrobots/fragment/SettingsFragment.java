package apps.com.astrobots.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import apps.com.astrobots.R;
import apps.com.astrobots.activity.AstroActivity;
import apps.com.astrobots.core.AstroConstants;
import apps.com.astrobots.core.AstroPreferences;

import static android.content.ContentValues.TAG;

/**
 * Created by kenji on 1/22/17.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {

    static final int RC_SIGN_IN = 1;

    GoogleApiClient mGoogleClient;
    RadioButton rbChannelId, rbChannelTitle;
    TextView tvSigninStatus;
    Button btnSubmit, btnSignout;
    SignInButton signInButton;

    boolean loginStatus = false;

    SharedPreferences sp;
    SharedPreferences.Editor mPrefs;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGoogleClient = ((AstroActivity) this.getActivity()).getClient();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.sp = getActivity().getApplicationContext().getSharedPreferences(AstroPreferences.PREF_FILE, Context.MODE_PRIVATE);
        this.mPrefs = sp.edit();
        this.loginStatus = sp.getBoolean(AstroPreferences.LOGIN_STATUS, false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        rbChannelId = (RadioButton) view.findViewById(R.id.rbChanId);
        rbChannelTitle = (RadioButton) view.findViewById(R.id.rbChanTitle);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnSignout = (Button) view.findViewById(R.id.btnSignout);
        btnSignout.setOnClickListener(this);
        tvSigninStatus = (TextView) view.findViewById(R.id.tvSigninStatus);
        // Set the dimensions of the sign-in button.
        signInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
        rbChannelId.setOnClickListener(this);
        rbChannelTitle.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        if(loginStatus){
            updateUI(true);
        }else{
            updateUI(false);
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            tvSigninStatus.setText(String.format("Hello, %s", acct.getDisplayName()));
            mPrefs.putString(AstroPreferences.ASTRO_CURRENT_USER,String.format("%s,%s,%s",acct.getId(),acct.getEmail(),acct.getPhotoUrl()));
            mPrefs.putBoolean(AstroPreferences.LOGIN_STATUS, true);
            mPrefs.putString(AstroPreferences.USER_ID,acct.getId());
            mPrefs.commit();
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        mPrefs.clear();
                        mPrefs.putBoolean(AstroPreferences.LOGIN_STATUS, false);
                        mPrefs.commit();
                        updateUI(false);
                    }
                });
    }

    private void updateUI(boolean status) {
        if (!status) {
            tvSigninStatus.setText("N/A");
            btnSignout.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.VISIBLE);
        } else {
            signInButton.setVisibility(View.INVISIBLE);
            btnSignout.setVisibility(View.VISIBLE);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.btnSignout:
                signOut();
                break;
            case R.id.rbChanId:
                rbChannelId.setChecked(true);
                rbChannelTitle.setChecked(false);
                break;
            case R.id.rbChanTitle:
                rbChannelId.setChecked(false);
                rbChannelTitle.setChecked(true);
                break;
            case R.id.btnSubmit:
                ChannelListFragment fragment = new ChannelListFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment);
                ChannelListFragment clf = new ChannelListFragment();
                if (rbChannelId.isChecked()) {
                    mPrefs.putInt(AstroPreferences.ASTRO_DEFAULT_SORT, AstroConstants.SORT_ID);
                    mPrefs.commit();
                    fragmentTransaction.commit();
                } else if (rbChannelTitle.isChecked()) {
                    mPrefs.putInt(AstroPreferences.ASTRO_DEFAULT_SORT, AstroConstants.SORT_NAME);
                    mPrefs.commit();
                    fragmentTransaction.commit();
                } else {
                    mPrefs.putInt(AstroPreferences.ASTRO_DEFAULT_SORT, AstroConstants.SORT_NAME);
                    mPrefs.commit();
                    Toast.makeText(getActivity(), "You have to select at least one", Toast.LENGTH_SHORT).show();
                    fragmentTransaction.commit();
                }
        }
    }
}
