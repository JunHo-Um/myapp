package com.example.upbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class TradingConfig {
    public String market;
    public double tradeAmountKrw;
    public int shortWindow;
    public int longWindow;
    public int pollingSeconds;
    public double maxDailyLossKrw;
    public double maxPositionExposureKrw;

    public static TradingConfig load(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(new File(path), TradingConfig.class);
    }
}