
package com.iconic.services.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Issue {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_ref")
    @Expose
    private String orderRef;
    @SerializedName("product_code")
    @Expose
    private String productCode;
    @SerializedName("date_issued")
    @Expose
    private String dateIssued;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(String dateIssued) {
        this.dateIssued = dateIssued;
    }

}
