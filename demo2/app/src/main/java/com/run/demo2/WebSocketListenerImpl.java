package com.run.demo2;

import android.util.Log;
import androidx.core.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketListenerImpl extends WebSocketListener {

    private final MainViewModel viewModel;
    private boolean isConnected = false;

    public WebSocketListenerImpl(MainViewModel viewModel) {
        this.viewModel = viewModel;
    }
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        Log.d("WebSocket", "Received message: " + text);

        try {
            // Parse the JSON data
            JSONObject jsonObject = new JSONObject(text);

            // Get the dataType and value
            String dataType = jsonObject.optString("dataType");
            String value = jsonObject.optString("value");

            String sensorData = null;

            // Check the dataType and handle accordingly
            if ("temperature_data".equals(dataType)) {
                double temperature = Double.parseDouble(value);
                sensorData = "Temperature: " + temperature + "°C";
            } else if ("humidity_data".equals(dataType)) {
                int humidity = Integer.parseInt(value);
                sensorData = "Humidity: " + humidity + "%";
            } else if ("gas_data".equals(dataType)) {
                int gasLevel = Integer.parseInt(value);
                sensorData = "Gas Level: " + gasLevel;
            }

            if (sensorData != null) {
                // Use ViewModel method to update the message
                viewModel.updateMessage(new Pair<>(false, sensorData));
            }
        } catch (JSONException e) {
            Log.e("WebSocket", "Failed to parse message: " + e.getMessage());
        }
    }

//    @Override
//    public void onMessage(WebSocket webSocket, String text) {
//        super.onMessage(webSocket, text);
//        Log.d("WebSocket", "Received message: " + text);
//        try {
//            // Parse the JSON data
//            JSONObject jsonObject = new JSONObject(text);
//
//            // Get the dataType and value
//            String dataType = jsonObject.optString("dataType");
//            String value = jsonObject.optString("value");
//
//            // Check the dataType and handle accordingly
//            if ("temperature_data".equals(dataType)) {
//                // Convert the value to a double for temperature
//                double temperature = Double.parseDouble(value);
//                String sensorData = "Temperature: " + temperature + "°C";
//                viewModel.setMessage(new Pair<>(false, sensorData));
//            } else if ("humidity_data".equals(dataType)) {
//                // Convert the value to an integer for humidity
//                int humidity = Integer.parseInt(value);
//                String sensorData = "Humidity: " + humidity + "%";
//                viewModel.setMessage(new Pair<>(false, sensorData));
//            } else if ("gas_data".equals(dataType)) {
//                // Convert the value to an integer for gas level
//                int gasLevel = Integer.parseInt(value);
//                String sensorData = "Gas Level: " + gasLevel;
//                viewModel.setMessage(new Pair<>(false, sensorData));
//            }
//        } catch (JSONException e) {
//            Log.e("WebSocket", "Failed to parse message: " + e.getMessage());
//        }
//    }



    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        isConnected = true;
        Log.d("WebSocket", "Connection opened: " + response.message());
        viewModel.setStatus(true); // Update connection status in ViewModel
//        webSocket.send("Android Device Connected.");
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        isConnected = false;
        Log.d("WebSocket", "Connection closed: " + reason);
        viewModel.setStatus(false); // Update connection status in ViewModel
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        isConnected = false;
        Log.e("WebSocket", "Connection failed: " + t.getMessage(), t);
        viewModel.setStatus(false); // Update connection status in ViewModel
        viewModel.updateMessage(new Pair<>(true, t.getMessage())); // Update error message
    }

    // Getter for connection status
    public boolean isConnected() {
        return isConnected;
    }
}
