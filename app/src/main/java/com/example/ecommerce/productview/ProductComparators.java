package com.example.ecommerce.productview;

import java.util.Comparator;

public class ProductComparators {
    public static class HighPriceComparator implements Comparator<ItemProduct> {
        @Override
        public int compare(ItemProduct p1, ItemProduct p2) {
            return Double.compare(Double.parseDouble(p2.getPrice()), Double.parseDouble(p1.getPrice()));
        }
    }

    public static class LowPriceComparator implements Comparator<ItemProduct> {
        @Override
        public int compare(ItemProduct p1, ItemProduct p2) {
            return Double.compare(Double.parseDouble(p1.getPrice()), Double.parseDouble(p2.getPrice()));
        }
    }

    public static class SignAZComparator implements Comparator<ItemProduct> {
        @Override
        public int compare(ItemProduct p1, ItemProduct p2) {
            return p1.getPname().compareToIgnoreCase(p2.getPname());
        }
    }

    public static class SignZAZComparator implements Comparator<ItemProduct> {
        @Override
        public int compare(ItemProduct p1, ItemProduct p2) {
            return p2.getPname().compareToIgnoreCase(p1.getPname());
        }
    }
}
