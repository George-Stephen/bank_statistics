package com.iconic.bank_statistics.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.iconic.bank_statistics.R;
import com.iconic.bank_statistics.adapters.CardAdapter;
import com.iconic.bank_statistics.adapters.OrderAdapter;
import com.iconic.services.StatsClient;
import com.iconic.services.StatsInterface;
import com.iconic.services.models.Branch;
import com.iconic.services.models.Country;
import com.iconic.services.models.Issue;
import com.iconic.services.models.Order;
import com.iconic.services.models.Product;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.country_spinner) Spinner mCountrySpinner;
    @BindView(R.id.branch_spinner) Spinner mBranchSpinner;
    @BindView(R.id.view_apps) TextView mViewApps;
    @BindView(R.id.view_applications) RecyclerView mViewApplications;
    @BindView(R.id.get_issues) Button mGetIssues;
    private CardAdapter adapter;

    public OrderFragment() {
        // Required empty public constructor
    }

    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_order, container, false);
       ButterKnife.bind(this,view);
       get_countries();
       get_branches();
       mGetIssues.setOnClickListener(this);
       return view;
    }

    @Override
    public void onClick(View v) {
        if (v ==  mGetIssues){
            String branch_code = mBranchSpinner.getSelectedItem().toString();
            String country_code = mCountrySpinner.getSelectedItem().toString();
            get_issues(country_code,branch_code);
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
                mCountrySpinner.setAdapter(adapter);
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
                mBranchSpinner.setAdapter(adapter);

            }

            @Override
            public void onFailure(@NotNull Call<List<Branch>> call, @NotNull Throwable t) {

            }
        });
    }
    private void get_issues(final String country_code, String branch_name){
        StatsInterface client = StatsClient.getClient();
        Call<List<Branch>> call = client.get_branch_code(branch_name);
        call.enqueue(new Callback<List<Branch>>() {
            @Override
            public void onResponse(@NotNull Call<List<Branch>> call, @NotNull Response<List<Branch>> response) {
                List<Branch> branches = response.body();
                String branch_code = branches.get(0).getBranchCode();
                StatsInterface client = StatsClient.getClient();
                Call<List<Issue>> call_1 = client.get_issues(branch_code,country_code);
                call_1.enqueue(new Callback<List<Issue>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NotNull Call<List<Issue>> call, @NotNull Response<List<Issue>> response) {
                        List<Issue> issues = response.body();
                        assert issues != null;
                        int issues_count = issues.size();
                        mViewApps.setText("All applications :" + issues_count);
                        adapter = new CardAdapter(getContext(),issues);
                        mViewApplications.setNestedScrollingEnabled(false);
                        mViewApplications.setAdapter(adapter);
                        mViewApplications.setLayoutManager(new LinearLayoutManager(getContext()));
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Issue>> call, @NotNull Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<Branch>> call, Throwable t) {

            }
        });
    }
}
