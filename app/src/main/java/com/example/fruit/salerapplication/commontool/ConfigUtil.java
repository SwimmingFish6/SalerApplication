package com.example.fruit.salerapplication.commontool;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by luxuhui on 2017/7/13.
 */

public class ConfigUtil {

    public static final String SERVER_CONFIG_FILENAME = "server.properties";

    public static Map<String, String> loadServerConfig(String filename, AssetManager assetManager) {
        AssetManager am = assetManager;
        Properties props = new Properties();
        try {
            InputStream inputStream = am.open(filename);
            props.load(inputStream);
            Map<String, String> result = new HashMap<>();
            for (Object key : props.keySet()) {
                result.put((String) key, (String) props.get(key));
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
