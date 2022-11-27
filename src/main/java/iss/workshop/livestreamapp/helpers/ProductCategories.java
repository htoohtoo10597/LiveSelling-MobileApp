package iss.workshop.livestreamapp.helpers;

import androidx.constraintlayout.compose.State;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum ProductCategories {
    CLOTHING, FOOD, APPLIANCES,
    FURNITURES, TECHNOLOGY, BABY,
    HEALTH, OTHERS, SPORTS, GROCERIES;

    public static String[] names() {
        String[] arrStr = Arrays.stream(ProductCategories.values())
                .map(e -> e.toString())
                .toArray(String[]::new);
        return arrStr;
    }
}

