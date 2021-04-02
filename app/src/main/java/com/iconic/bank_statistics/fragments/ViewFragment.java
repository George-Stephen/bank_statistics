package com.iconic.bank_statistics.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.iconic.bank_statistics.R;
import com.iconic.bank_statistics.adapters.CardAdapter;
import com.iconic.bank_statistics.adapters.ViewAdapter;
import com.iconic.services.StatsClient;
import com.iconic.services.StatsInterface;
import com.iconic.services.models.Branch;
import com.iconic.services.models.Country;
import com.iconic.services.models.Issue;
import com.iconic.services.models.Order;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ViewFragment extends Fragment  implements View.OnClickListener  {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.branch_display_spinner) Spinner mDisplayBranch;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.country_display_spinner) Spinner mCountryBranch;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.get_order) Button mGetOrder;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.order_progress) ProgressBar mCardProgress;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.display_order) RecyclerView mViewOrders;
    private ViewAdapter adapter;


    public ViewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);
        ButterKnife.bind(this,view);
        get_countries();
        get_branches();
        mGetOrder.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == mGetOrder){
            String country = mCountryBranch.getSelectedItem().toString();
            String branch = mDisplayBranch.getSelectedItem().toString();
            get_issues(country,branch);
        }
    }

    private void get_countries(){
        StatsInterface client = StatsClient.getClient();
        Call<List<Country>> call = client.get_country();
        call.enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(@NotNull Call<List<Country>> call, @NotNull Response<List<Country>> response) {
                List<Country> countries = response.body();
                List<String> country_names = new ArrayList<>();
                assert countries != null;
                for (Country country : countries){
                    String c_name = country.getCountryCode();
                    country_names.add(c_name);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()),android.R.layout.simple_spinner_item,country_names);
                mCountryBranch.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NotNull Call<List<Country>> call, @NotNull Throwable t) {
            }
        });
    }
    private void get_branches(){
        StatsInterface client = StatsClient.getClient();
        Call<List<Branch>> call = client.get_branch();
        call.enqueue(new Callback<List<Branch>>() {
            @Override
            public void onResponse(@NotNull Call<List<Branch>> call, @NotNull Response<List<Branch>> response) {
                List<Branch> branches = response.body();
                List<String> branch_name = new ArrayList<>();
                assert branches != null;
                for (Branch branch : branches){
                    String text = branch.getBranchName();
                    branch_name.add(text);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()),android.R.layout.simple_spinner_item,branch_name);
                mDisplayBranch.setAdapter(adapter);

            }

            @Override
            public void onFailure(@NotNull Call<List<Branch>> call, @NotNull Throwable t) {

            }
        });
    }
    private void get_issues(final String country_code, final String branch_name){
        StatsInterface client = StatsClient.getClient();
        Call<List<Branch>> call = client.get_branch_code(branch_name);
        call.enqueue(new Callback<List<Branch>>() {
            @Override
            public void onResponse(@NotNull Call<List<Branch>> call, @NotNull Response<List<Branch>> response) {
                show_progress();
                List<Branch> branches = response.body();
                String branch_code = branches.get(0).getBranchCode();
                StatsInterface client = StatsClient.getClient();
                Call<List<Order>> call_1 = client.get_orders(branch_code,country_code);
                call_1.enqueue(new Callback<List<Order>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Order>> call, @NotNull Response<List<Order>> response) {
                        List<Order> orders = response.body();
                        if (orders.isEmpty()){
                            mCardProgress.setVisibility(View.GONE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(branch_name + " branch has no orders");
                            builder.setTitle("No data available");
                            builder.setCancelable(true);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }else{
                            adapter = new ViewAdapter(orders,getContext());
                            mViewOrders.setNestedScrollingEnabled(false);
                            mViewOrders.setAdapter(adapter);
                            mViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
                            show_applications();
                        }

                    }

                    @Override
                    public void onFailure(Call<List<Order>> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<Branch>> call, Throwable t) {

            }
        });
    }
    private void show_progress(){
        mCardProgress.setVisibility(View.VISIBLE);
    }
    private void show_applications(){
        mCardProgress.setVisibility(View.GONE);
        mViewOrders.setVisibility(View.VISIBLE);
    }
}