package com.iconic.bank_statistics.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iconic.bank_statistics.R;
import com.iconic.services.StatsClient;
import com.iconic.services.StatsInterface;
import com.iconic.services.models.Issue;
import com.iconic.services.models.Order;
import com.iconic.services.models.Product;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardHolder> {
    private Context context;
    private List<Issue> issues;

    public CardAdapter(Context context, List<Issue> issues) {
        this.context = context;
        this.issues = issues;
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item,parent,false);
        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        Issue issue =  issues.get(position);
        holder.bind_card(issue);
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }


    static class CardHolder extends RecyclerView.ViewHolder {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.issue_name) TextView mIssueName;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.issue_date) TextView mIssueDate;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.issue_account) TextView mIssueAccount;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.issue_status) TextView mIssueStatus;

         public CardHolder(@NonNull View itemView) {
             super(itemView);
             ButterKnife.bind(this,itemView);
         }


         public void bind_card(final Issue issue){
             String product_code = issue.getProductCode();
             StatsInterface client = StatsClient.getClient();
             Call<List<Product>> call = client.get_product(product_code);
             call.enqueue(new Callback<List<Product>>() {
                 @Override
                 public void onResponse(@NotNull Call<List<Product>> call, @NotNull Response<List<Product>> response) {
                     List<Product> products = response.body();
                     assert products != null;
                     Product product = products.get(0);
                     mIssueName.setText(product.getProductName());
                     mIssueDate.setText(issue.getDateIssued());
                     mIssueAccount.setText(issue.getAccountName());
                     mIssueStatus.setText(issue.getStatus());
                 }

                 @Override
                 public void onFailure(@NotNull Call<List<Product>> call, @NotNull Throwable t) {

                 }
             });
         }
     }
}
