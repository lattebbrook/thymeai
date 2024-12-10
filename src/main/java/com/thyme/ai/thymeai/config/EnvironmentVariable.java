package com.thyme.ai.thymeai.config;

public class EnvironmentVariable {

    //token, api access
    public static final String RUNWAY_ML_TOKEN = System.getenv("RUNWAY_ML_TOKEN");
    public static final String LINE_BOT_CHANNEL_TOKEN = System.getenv("LINE_BOT_CHANNEL_TOKEN");
    //public static final String LINE_BOT_CHANNEL_SECRET = System.getenv("LINE_BOT_CHANNEL_SECRET");

    //cdn url
    public static final String LINE_CDN_URL = System.getenv("LINE_CDN_URL");
    public static final String RUNWAY_ML_CDN_URL = System.getenv("RUNWAY_ML_CDN_URL");


}
