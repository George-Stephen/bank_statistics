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
public class OrderFragment extends Fragment {
    @BindView(R.id.total_orders) TextView mTotalOrders;
    @BindView(R.id.order_spinner) Spinner mOrderProduct;
    @BindView(R.id.country_order_spinner) Spinner mCountrySpinner;
    @BindView(R.id.get_orders) Button mGetOrders;

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
       getProductSpinner();
       mGetOrders.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String product_code = mOrderProduct.getSelectedItem().toString();
               String country_code = mCountrySpinner.getSelectedItem().toString();
               get_orders(product_code,country_code);
           }
       });
       return view;
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
                mOrderProduct.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(@NotNull Call<List<Product>> call, @NotNull Throwable t) {

            }
        });
    }
    private void get_orders(String product_name, final String country_code){
        StatsInterface client = StatsClient.getClient();
        Call<List<Product>> product_call = client.get_card(product_name);
        product_call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                List<Product> products = response.body();
                String product_code = products.get(0).getProductCode();
                StatsInterface client = StatsClient.getClient();
                Call<List<Order>> order_call = client.get_order(product_code,country_code);
                order_call.enqueue(new Callback<List<Order>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                        List<Order> orders = new ArrayList<>();
                        orders = response.body();
                        assert orders != null;
                        String total = Integer.toString(orders.size());
                        mTotalOrders.setText("Number of orders " + ":" + total);
                    }

                    @Override
                    public void onFailure(Call<List<Order>> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }
}
