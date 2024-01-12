package com.example.ecommerce.emailfeedback;
public class ReceiptItem {
    private String receiptName;
    private int receiptCount;
    private double receiptPrice;

    public ReceiptItem(String receiptName, int receiptCount, double receiptPrice) {
        this.receiptName = receiptName;
        this.receiptCount = receiptCount;
        this.receiptPrice = receiptPrice;
    }

    public String getReceiptName() {
        return receiptName;
    }

    public int getReceiptCount() {
        return receiptCount;
    }

    public double getReceiptPrice() {
        return receiptPrice;
    }
}
