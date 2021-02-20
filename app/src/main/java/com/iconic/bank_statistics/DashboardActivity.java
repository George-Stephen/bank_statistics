package com.iconic.bank_statistics;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.app_activity)
    Button mAppButton;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.inventory_activity)
    Button mInventoryButton;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.order_activity)
    Button mOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        mAppButton.setOnClickListener(this);
        mInventoryButton.setOnClickListener(this);
        mOrderButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mAppButton) {
            Intent intent = new Intent(this, OrderActivity.class);
            startActivity(intent);
            finish();
        } else if (v == mInventoryButton) {
            Intent intent = new Intent(this, CardActivity.class);
            startActivity(intent);
            finish();
        } else if (v == mOrderButton) {
            Intent intent = new Intent(this, ViewActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.log_out) {
            Toast.makeText(getApplicationContext(), "You have logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}