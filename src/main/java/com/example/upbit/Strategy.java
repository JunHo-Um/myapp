package com.example.upbit;

import java.util.List;

public interface Strategy {
    OrderDecision evaluate(List<Double> prices);
}