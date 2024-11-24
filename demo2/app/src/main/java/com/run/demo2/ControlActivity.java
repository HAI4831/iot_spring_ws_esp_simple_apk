package com.run.demo2;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class ControlActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private WebSocket webSocket;
    private WebSocketListenerImpl listener;
    private int speed = 10; // Default speed
    private final Handler speedHandler = new Handler();
    private final Handler pingHandler = new Handler(); // Handler for ping
    private ImageView connectionIcon; // Icon to show connection status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        // Initialize ViewModel using ViewModelProvider
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        connectionIcon = findViewById(R.id.connectionIcon); // Initialize the connection icon
        listener = new WebSocketListenerImpl(viewModel);
        OkHttpClient client = new OkHttpClient();

        // Create WebSocket connection
        Request request = new Request.Builder().url("wss://messagingstompwebsocket-latest.onrender.com:443/move").build();
//        Request request = new Request.Builder().url("ws://192.168.0.104:8060/move").build();
        webSocket = client.newWebSocket(request, listener);

        Button btnForward = findViewById(R.id.btnForward);
        Button btnBackward = findViewById(R.id.btnBackward);
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);
        Button btnSpeed = findViewById(R.id.btnSpeed);

        setupActionButton(btnForward, "FORWARD");
        setupActionButton(btnBackward, "BACKWARD");
        setupActionButton(btnLeft, "LEFT");
        setupActionButton(btnRight, "RIGHT");

        // Lắng nghe thay đổi trong ViewModel và cập nhật UI
        viewModel.getMessage().observe(this, new Observer<Pair<Boolean, String>>() {
            @Override
            public void onChanged(Pair<Boolean, String> message) {
                Log.d("onChanged:" , String.valueOf(message));
                if (message != null && message.second != null) {
                    String sensorData = message.second;

                    // Check for sensor data type and set values on appropriate TextViews
                    if (sensorData.contains("Temperature")) {
                        // Set the temperature TextView
                        TextView temperatureTextView = findViewById(R.id.temperature);
                        temperatureTextView.setText(sensorData);
                    } else if (sensorData.contains("Humidity")) {
                        // Set the humidity TextView
                        TextView humidityTextView = findViewById(R.id.humidity);
                        humidityTextView.setText(sensorData);
                    } else if (sensorData.contains("Gas Level")) {
                        // Set the gas level TextView
                        TextView gasLevelTextView = findViewById(R.id.gas_level);
                        gasLevelTextView.setText(sensorData);
                    }
                }
            }
        });

        btnSpeed.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                speedHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        speed += 5; // Increase speed every 500ms
                        showToast("Speed increased to: " + speed); // Show speed increase toast
                        speedHandler.postDelayed(this, 500); // Continue increasing speed
                    }
                }, 500);
                v.performClick(); // Ensure accessibility actions
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                speedHandler.removeCallbacksAndMessages(null); // Stop increasing speed on release
                showToast("Speed stopped"); // Show speed stop toast
            }
            return true;
        });
        tryConnect();

        // Start pinging every 15 seconds if WebSocket is connected
//        startPing();
    }

    private void tryConnect() {
        Handler reconnectHandler = new Handler();
        reconnectHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!listener.isConnected()) {
                    connectionIcon.setImageResource(R.drawable.baseline_sync_disabled_24);
                    Log.d("WebSocket", "Attempting to reconnect...");
                    OkHttpClient client = new OkHttpClient();
//                    Request request = new Request.Builder().url("ws://192.168.0.104:8060/move").build();
                    Request request = new Request.Builder().url("wss://messagingstompwebsocket-latest.onrender.com:443/move").build();
                    Log.d("WebSocket ", listener.isConnected() ? "WebSocket Connected" : "WebSocket Disconnected");
                    webSocket = client.newWebSocket(request, listener);

                    // Tiếp tục thử kết nối ngay lập tức nếu chưa kết nối thành công
                    reconnectHandler.postDelayed(this, 1000); // Thử lại sau 1 giây
                } else {
                    connectionIcon.setImageResource(R.mipmap.ic_ws);
                    Log.d("WebSocket", "Connection established.");
                }
            }
        }, 1000); // Bắt đầu thử kết nối sau 1 giây đầu tiên
    }


    private void setupActionButton(Button button, String actionMoveName) {
        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendMoveAction(actionMoveName, speed); // Send move action when button is pressed
                showToast(actionMoveName + " action started");
                v.performClick(); // Ensure accessibility actions
            }
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendMoveAction("STOP", speed); // Send stop action on release
                showToast("STOP action executed");
            }
            return true;
        });
    }

    private void sendMoveAction(String actionMoveName, int speed) {
        if (webSocket != null && listener.isConnected()) {
            // Tạo thông điệp
            String message = String.format("{\"action_move_name\":\"%s\",\"speed\":%d}", actionMoveName, speed);
            Log.d("WebSocket", "Sending message: " + message); // Log tin nhắn
            webSocket.send(message); // Gửi tin nhắn
        } else {
            Log.d("WebSocket", "WebSocket is not connected");
            showToast("WebSocket is not connected"); // Hiển thị thông báo lỗi khi không kết nối được WebSocket
        }
    }

    private void startPing() {
        pingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (webSocket != null && listener.isConnected()) {
//                    sendPing(); // Send ping
                    pingHandler.postDelayed(this, 15000); // Repeat ping every 15 seconds
                }
            }
        }, 15000); // First ping after 15 seconds
    }

    private void sendPing() {
        if (webSocket != null) {
            String pingMessage = "{\"ping\":\"true\"}"; // Define the ping message
            webSocket.send(pingMessage); // Send ping message to server
            Log.d("WebSocket", "Sending Ping: " + pingMessage);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); // Show toast message
    }
}
