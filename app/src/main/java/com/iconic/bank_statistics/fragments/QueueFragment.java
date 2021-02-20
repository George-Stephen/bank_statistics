package com.iconic.bank_statistics.fragments;

import android.annotation.SuppressLint;
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
import com.iconic.bank_statistics.adapters.QueueAdapter;
import com.iconic.services.StatsClient;
import com.iconic.services.StatsInterface;
import com.iconic.services.models.Branch;
import com.iconic.services.models.Country;
import com.iconic.services.models.Issue;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QueueFragment extends Fragment implements View.OnClickListener {

   @SuppressLint("NonConstantResourceId")
   @BindView(R.id.branch_queue_spinner) Spinner mBranchSpinner;
   @SuppressLint("NonConstantResourceId")
   @BindView(R.id.country_queue_spinner) Spinner mCountrySpinner;
   @SuppressLint("NonConstantResourceId")
   @BindView(R.id.get_queues) Button mGetQueue;
   @SuppressLint("NonConstantResourceId")
   @BindView(R.id.queue_progress) ProgressBar mQueueProgress;
   @SuppressLint("NonConstantResourceId")
   @BindView(R.id.display_queues) RecyclerView mDisplayQueue;
   private QueueAdapter adapter;

    public QueueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue, container, false);
        ButterKnife.bind(this,view);
        get_branches();
        get_countries();
        mGetQueue.setOnClickListener(this);
        return view;
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

    @Override
    public void onClick(View v) {
        if (v == mGetQueue){
            String country_code = mCountrySpinner.getSelectedItem().toString();
            String branch_code = mBranchSpinner.getSelectedItem().toString();
            get_queue(country_code,branch_code);
        }
    }
    private void get_queue(String country,String branch){
        StatsInterface client = StatsClient.getClient();
        Call<List<Issue>> call = client.get_issues(branch,country);
        call.enqueue(new Callback<List<Issue>>() {
            @Override
            public void onResponse(@NotNull Call<List<Issue>> call, @NotNull Response<List<Issue>> response) {
                show_progress();
                if (response.isSuccessful()){
                    List<Issue> issues = response.body();
                    adapter = new QueueAdapter(issues,getContext());
                    mDisplayQueue.setNestedScrollingEnabled(false);
                    mDisplayQueue.setAdapter(adapter);
                    mDisplayQueue.setLayoutManager(new LinearLayoutManager(getContext()));
                    show_applications();
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Issue>> call, @NotNull Throwable t) {

            }
        });
    }

    private void show_progress(){
        mQueueProgress.setVisibility(View.VISIBLE);
    }
    private void show_applications(){
        mQueueProgress.setVisibility(View.GONE);
        mDisplayQueue.setVisibility(View.VISIBLE);
    }

}