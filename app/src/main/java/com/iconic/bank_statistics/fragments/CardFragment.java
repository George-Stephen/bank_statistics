package com.iconic.bank_statistics.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.iconic.bank_statistics.R;
import com.iconic.services.StatsClient;
import com.iconic.services.StatsInterface;
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
public class CardFragment extends Fragment {
    @BindView(R.id.cards_chart) PieChartView mCardsChart;
    @BindView(R.id.product_spinner) Spinner mProductSpinner;
    @BindView(R.id.country_spinner) Spinner mCountrySpinner;
    @BindView(R.id.get_stats) Button mGetStats;
    @BindView(R.id.remaining_cards) TextView mRemainingCards;
    @BindView(R.id.issued_cards) TextView mIssuedCards;
    List<SliceValue> pie_data = new ArrayList<>();


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
        getProductSpinner();
        mGetStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String product_code ="";
                product_code = mProductSpinner.getSelectedItem().toString();
                String country_code = mCountrySpinner.getSelectedItem().toString();
                get_orders(product_code,country_code);
            }
        });
        return view;
    }

   private void get_orders(final String product_name, final String country_code){
        StatsInterface client = StatsClient.getClient();
        Call<List<Product>> product_call = client.get_card(product_name);
        product_call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                List<Product> productList = response.body();
                assert productList != null;
                final String product_code  = productList.get(0).getProductCode();
                StatsInterface client = StatsClient.getClient();
                Call<List<Order>> order_call = client.get_order(product_code,country_code);
                order_call.enqueue(new Callback<List<Order>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Order>> call, @NotNull Response<List<Order>> response) {
                        List<Order>orders= response.body();
                        int sum = 0;
                        assert orders != null;
                        for (Order order : orders){
                            sum+= order.getOrderQty();
                        }
                        StatsInterface client = StatsClient.getClient();
                        Call<List<Issue>> call_2 = client.get_issues(product_code);
                        final int finalSum = sum;
                        call_2.enqueue(new Callback<List<Issue>>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onResponse(@NotNull Call<List<Issue>> call, @NotNull Response<List<Issue>> response) {
                                assert response.body() != null;
                                int total_issues = response.body().size();
                                int rem = finalSum - total_issues;
                                float rem_percent =(float) ((rem*100) / finalSum);
                                float issue_percent = (float) ((total_issues * 100) / finalSum);
                                pie_data.add(new SliceValue(rem_percent,Color.RED).setLabel("Remaining cards " + ":" + rem));
                                pie_data.add(new SliceValue(issue_percent,Color.BLUE).setLabel("Issued cards " + ":" + total_issues));
                                mIssuedCards.setText("Issued cards "+ ":" + total_issues);
                                mRemainingCards.setText("Remaining cards "+":"+ rem);
                                PieChartData chartData = new PieChartData(pie_data);
                                chartData.setHasLabels(true).setValueLabelTextSize(12);
                                chartData.setHasCenterCircle(true).setCenterText1("Number of cards").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));;
                                mCardsChart.setPieChartData(chartData);

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
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }
    private void getProductSpinner(){
        StatsInterface client = StatsClient.getClient();
        Call<List<Product>> call = client.get_cards();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NotNull Call<List<Product>> call, @NotNull Response<List<Product>> response) {
                List<Product> products = response.body();
                List<String> product_id_s = new ArrayList<>();
                product_id_s.add("Select the product type");
                assert products != null;
                for (Product product : products){
                    String text = product.getProductName();
                    product_id_s.add(text);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, product_id_s);
                mProductSpinner.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(@NotNull Call<List<Product>> call, @NotNull Throwable t) {

            }
        });
    }
}
