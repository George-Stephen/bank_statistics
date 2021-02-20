package com.iconic.bank_statistics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.register_name) EditText mRegisterName;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.register_phone) EditText mRegisterPhone;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.register_email) EditText mRegisterEmail;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.register_password) EditText mRegisterPassword;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.confirm_password) EditText mConfirmPassword;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.register_button) Button mRegisterButton;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.login_text) FloatingActionButton mLoginText;
    private ProgressDialog mAuthProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        createAuthProgressDialog();
        mRegisterButton.setOnClickListener(this);
        mLoginText.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v == mLoginText){
            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if (v == mRegisterButton){
            String full_name = mRegisterName.getText().toString();
            String phone = mRegisterPhone.getText().toString();
            String email_address = mRegisterEmail.getText().toString();
            String password = mRegisterPassword.getText().toString();
            String confirm_password = mRegisterPassword.getText().toString();
            boolean validEmail = isValidEmail(email_address);
            boolean validPassword = isValidPassword(password,confirm_password);
            if (!validEmail || !validPassword) return;
            try {
                password = Security.getHashPassword(password, Constants.password_salt.getBytes());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            add_manager(full_name,phone,email_address,password);
        }
    }
    private void add_manager(final String full_name, String phone, String email_address, String password){
        mAuthProgress.show();
        StatsInterface client = StatsClient.getClient();
        Call<Manager> call = client.add_manager(full_name,phone,email_address,password);
        call.enqueue(new Callback<Manager>() {
            @Override
            public void onResponse(@NotNull Call<Manager> call, @NotNull Response<Manager> response) {
                mAuthProgress.hide();
                if (response.isSuccessful()){
                    Toast.makeText(RegisterActivity.this,"Welcome " + full_name + " ;",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Manager not added",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<Manager> call, @NotNull Throwable t) {
                mAuthProgress.hide();
                Toast.makeText(RegisterActivity.this,"Your internet is not stable,try again later",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean isValidEmail(String email){
        boolean isGoodEmail = (email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if(!isGoodEmail){
            mRegisterEmail.setError("Please enter a valid email Address");
            return false;
        }
        return true;
    }
    private boolean isValidPassword(String Password,String confirmPassword){
        if (Password.length() < 6) {
            mRegisterPassword.setError("The password should be at least 6 Characters");
            return false;

        }else if(!Password.equals(confirmPassword)){
            mRegisterPassword.setError("Passwords don't match");
            return false;
        }
        return true;
    }

    private void createAuthProgressDialog(){
        mAuthProgress  = new ProgressDialog(this);
        mAuthProgress.setTitle("Loading...");
        mAuthProgress.setMessage("Creating your account....");
        mAuthProgress.setCancelable(false);
    }
}
