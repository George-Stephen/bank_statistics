package com.iconic.bank_statistics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iconic.bank_statistics.hashing.Security;
import com.iconic.services.Constants;
import com.iconic.services.StatsClient;
import com.iconic.services.StatsInterface;
import com.iconic.services.models.Manager;

import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.login_email) EditText mLoginEmail;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.login_password) EditText mLoginPassword;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.login_button) FloatingActionButton mLoginButton;
    private ProgressDialog mAuthProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mLoginButton.setOnClickListener(this);
        createAuthProgressDialog();
    }

    @Override
    public void onClick(View v) {
        if (v == mLoginButton){
            if (mLoginEmail.getText().toString().equals("")){
                mLoginEmail.setError("Please enter your Email address");
            }
            if (mLoginPassword.getText().toString().equals("")){
                mLoginPassword.setError("Please enter your password");
            }else {
                String email_address = mLoginEmail.getText().toString();
                byte[] salt = Constants.password_salt.getBytes();
                String password = mLoginPassword.getText().toString();
                try {
                    password = Security.getHashPassword(password, salt);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                get_manager(email_address,password);
            }

        }
    }
    private void get_manager(String email_address,String password){
        mAuthProgressDialog.show();
        StatsInterface client = StatsClient.getClient();
        Call<List<Manager>> call = client.get_manager(email_address,password);
        call.enqueue(new Callback<List<Manager>>() {
            @Override
            public void onResponse(@NotNull Call<List<Manager>> call, @NotNull Response<List<Manager>> response) {
                mAuthProgressDialog.hide();
                if (response.isSuccessful()){
                    List<Manager> managers = response.body();
                    assert managers != null;
                    Manager manager = managers.get(0);
                    Toast.makeText(LoginActivity.this,"Welcome back " + manager.getFullName() + " ;",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this,DashboardActivity.class);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Manager>> call, @NotNull Throwable t) {
                mAuthProgressDialog.hide();
                Toast.makeText(LoginActivity.this,"Your internet is not stable,try again later",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void createAuthProgressDialog(){
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Loading account");
        mAuthProgressDialog.setCancelable(false);
    }

}
