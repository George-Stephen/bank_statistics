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
import android.widget.ProgressBar;
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
 * Use the {@link CardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardFragment extends Fragment implements View.OnClickListener{
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.view_applications) RecyclerView mIssueList;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.view_chart) PieChartView mIssueChart;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.app_progress) ProgressBar mAppProgress;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.country_order_spinner) Spinner mCountrySpinner;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.branch_order_spinner) Spinner mBranchSpinner;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.get_applications) Button mGetButton;
    List< SliceValue > pieData = new ArrayList<>();
    private OrderAdapter adapter;


    public CardFragment() {
        // Required empty public constructor
    }
    public static CardFragment newInstance(String param1, String param2) {
        CardFragment fragment = new CardFragment();
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
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        ButterKnife.bind(this,view);
        get_countries();
        get_branches();
        mGetButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == mGetButton){
            String country_code = mCountrySpinner.getSelectedItem().toString();
            String branch_name = mBranchSpinner.getSelectedItem().toString();
            get_cardApplication(country_code,branch_name);
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

    private void get_cardApplication(final String country_code, String branch_name){
            StatsInterface client = StatsClient.getClient();
            Call<List<Branch>> call = client.get_branch_code(branch_name);
            call.enqueue(new Callback<List<Branch>>() {
                @Override
                public void onResponse(@NotNull Call<List<Branch>> call, @NotNull Response<List<Branch>> response) {
                    List<Branch> branches = response.body();
                    assert branches != null;
                    Branch branch = branches.get(0);
                    final String branch_code = branch.getBranchCode();
                    StatsInterface statsInterface = StatsClient.getClient();
                    Call<List<Order>> call_1 = statsInterface.get_issued_orders(branch_code,country_code,"dispatched");
                    call_1.enqueue(new Callback<List<Order>>() {
                        @Override
                        public void onResponse(@NotNull Call<List<Order>> call, @NotNull Response<List<Order>> response) {
                            show_progress();
                            List<Order> orders = response.body();
                            adapter = new OrderAdapter(getContext(),orders);
                            mIssueList.setAdapter(adapter);
                            mIssueList.setNestedScrollingEnabled(false);
                            mIssueList.setLayoutManager(new LinearLayoutManager(getContext()));
                            show_applications();

                            int sum = 0;
                            assert orders != null;
                            for (Order order : orders){
                                sum+=order.getOrderQty();
                            }
                            StatsInterface client = StatsClient.getClient();
                            Call<List<Issue>> call_2 = client.get_issues(branch_code,country_code);
                            final int finalSum = sum;
                            call_2.enqueue(new Callback<List<Issue>>() {
                                @Override
                                public void onResponse(@NotNull Call<List<Issue>> call, @NotNull Response<List<Issue>> response) {
                                    List<Issue> issues = response.body();
                                    assert issues != null;
                                    int issue_quantity = issues.size();
                                    int remaining = finalSum - issue_quantity;
                                    double issue_percent = (double) ((issue_quantity * 100) / finalSum);
                                    double rem_percent = (double) ((remaining * 100) / finalSum);
                                    pieData.add(new SliceValue((float) issue_percent, Color.parseColor("#8FBC8F")).setLabel(String.valueOf(issue_quantity)));
                                    pieData.add(new SliceValue((float) rem_percent, Color.parseColor("#D3D3D3")).setLabel(String.valueOf(remaining)));
                                    PieChartData pieChartData = new PieChartData(pieData);
                                    pieChartData.setHasLabels(true);
                                    pieChartData.setHasCenterCircle(true).setCenterText1("Cards issued").setCenterText1FontSize(12).setCenterText1Color(Color.parseColor("#0097A7"));
                                    mIssueChart.setPieChartData(pieChartData);
                                }

                                @Override
                                public void onFailure(@NotNull Call<List<Issue>> call, @NotNull Throwable t) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(@NotNull Call<List<Order>> call, @NotNull Throwable t) {

                        }
                    });

                }

                @Override
                public void onFailure(@NotNull Call<List<Branch>> call, @NotNull Throwable t) {

                }
            });
    }

    private void show_progress(){
        mAppProgress.setVisibility(View.VISIBLE);
    }

    private void show_applications(){
        mAppProgress.setVisibility(View.GONE);
        mIssueList.setVisibility(View.VISIBLE);
    }
}
