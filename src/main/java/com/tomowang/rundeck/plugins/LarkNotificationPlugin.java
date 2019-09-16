package com.tomowang.rundeck.plugins;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.descriptions.TextArea;
import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * refer doc
 * https://getfeishu.cn/hc/zh-cn/articles/360024984973-%E5%9C%A8%E7%BE%A4%E8%81%8A%E4%B8%AD%E4%BD%BF%E7%94%A8%E6%9C%BA%E5%99%A8%E4%BA%BA
 */
@Plugin(service = "Notification", name = "lark")
@PluginDescription(title = "Lark", description = "Lark robot webhook trigger")
public class LarkNotificationPlugin implements NotificationPlugin {
    @PluginProperty(name = "access_token", title = "Access Token", required = true)
    private String access_token;

    @PluginProperty(name = "title", title = "Title")
    private String title;

    @PluginProperty(name = "text", title = "Body",
            description = "Notification Body, can have simple HTML tag like <a> and <p>")
    @TextArea
    private String text;

    /**
     * Post a notification for the given trigger, dataset, and configuration
     *
     * @param trigger       event type causing notification
     * @param executionData execution data
     * @param config        notification configuration
     * @return true if successul
     */
    public boolean postNotification(String trigger, Map executionData, Map config) {
        String hook = "https://open.feishu.cn/open-apis/bot/hook/" + access_token;

        JsonBody body = new JsonBody(title, text);
        Gson gson = new Gson();
        String payload = gson.toJson(body);
        URL url;
        try {
            url = new URL(hook);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            byte[] input = payload.getBytes("utf-8");
            os.write(input, 0, input.length);
            int code = con.getResponseCode();

            if (code >= 300) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    static class JsonBody {
        String title;
        String text;

        public JsonBody(String title, String text) {
            this.title = title;
            this.text = text;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}