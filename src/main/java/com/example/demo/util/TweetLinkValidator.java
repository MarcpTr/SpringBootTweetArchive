package com.example.demo.util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class TweetLinkValidator {

    private static final String TWEET_URL_REGEX = "^(https?:\\/\\/(?:www\\.)?(?:x\\.com|twitter\\.com)\\/([A-Za-z0-9_]+)\\/status\\/\\d+|(?:www\\.)?(?:x\\.com|twitter\\.com)\\/([A-Za-z0-9_]+)\\/status\\/\\d+)$";

    public static boolean isTweetUrl(String url) {
        Pattern pattern = Pattern.compile(TWEET_URL_REGEX);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }
}
