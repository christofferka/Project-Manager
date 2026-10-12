package com.example.projectcalctool.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CostItemTest {

    @Test
    @DisplayName("getTotal multiplies quantity by unitPrice")
    void getTotal_multipliesQuantityByUnitPrice() {
        CostItem item = new CostItem(1, 1, "Licens", 3.0, 250.0);
        assertEquals(750.0, item.getTotal());
    }

    @Test
    @DisplayName("getTotal returns 0 when quantity is 0")
    void getTotal_zeroQuantity() {
        CostItem item = new CostItem();
        item.setUnitPrice(99.0);
        assertEquals(0.0, item.getTotal());
    }
}
