package com.iconic.bank_statistics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iconic.services.StatsClient;
import com.iconic.services.StatsInterface;
import com.iconic.services.models.Manager;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.register_name) EditText mRegisterName;
    @BindView(R.id.register_phone) EditText mRegisterPhone;
    @BindView(R.id.register_email) EditText mRegisterEmail;
    @BindView(R.id.register_center) EditText mRegisterCenter;
    @BindView(R.id.register_password) EditText mRegisterPassword;
    @BindView(R.id.confirm_password) EditText mConfirmPassword;
    @BindView(R.id.register_button) Button mRegisterButton;
    @BindView(R.id.login_text) TextView mLoginText;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mAuthProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        createAuthStateListener();
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
            String center = mRegisterCenter.getText().toString();
            String password = mRegisterPassword.getText().toString();
            String confirm_password = mConfirmPassword.getText().toString();
            boolean validEmail = isValidEmail(email_address);
            boolean validPassword = isValidPassword(password,confirm_password);
            if (!validEmail || !validPassword) return;
            mAuthProgress.show();
            mAuth.createUserWithEmailAndPassword(email_address,password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mAuthProgress.hide();
                            if (task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this,"Authentication success",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            add_manager(full_name,phone,email_address,center);
        }
    }
    private void add_manager(String full_name,String phone,String email_address,String center){
        StatsInterface client = StatsClient.getClient();
        Call<Manager> call = client.add_manager(full_name,phone,email_address,center);
        call.enqueue(new Callback<Manager>() {
            @Override
            public void onResponse(@NotNull Call<Manager> call, @NotNull Response<Manager> response) {
                if (response.isSuccessful()){
                    Toast.makeText(RegisterActivity.this,"Manager added successfully",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Manager not added",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<Manager> call, @NotNull Throwable t) {
                Toast.makeText(RegisterActivity.this,"Please try again later",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void createAuthStateListener(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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
