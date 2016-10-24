package com.shihui.openpf.living.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private HttpUtil() {}

    public static String doGet(String url) throws IOException {
        return doGet(new URL(url));
    }

    public static String doGet(URL url) throws IOException {
        logger.info("request: {}", url.toString());
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setRequestMethod("GET");
            con.setUseCaches(false);
            con.setDoOutput(false);
            con.setDoInput(true);
            //
            StringBuilder response = new StringBuilder();
            String line = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            logger.info("response: {}", response);
            return response.toString();
        } catch (IOException e) {
            logger.error(url.toString(), e);
            throw e;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    public static String doPost(URL url, Map<?, ?> param) throws IOException {
        logger.info("request: {} {}", url.toString(), param);
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setRequestMethod("POST");
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            for (Entry<?, ?> entry : param.entrySet()) {
                out.write("&" + entry.getKey() + "=" + entry.getValue());
            }
            out.flush();
            //
            StringBuilder response = new StringBuilder();
            String line = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            logger.info("response: {}", response);
            return response.toString();
        } catch (IOException e) {
            logger.error(url.toString(), e);
            throw e;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
}
