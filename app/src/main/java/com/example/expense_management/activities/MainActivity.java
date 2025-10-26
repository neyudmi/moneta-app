package com.example.expense_management.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.expense_management.BuildConfig;
import com.example.expense_management.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private TextInputLayout layoutEmail, layoutPassword;
    private MaterialButton btnLogin, btnForgot;
    private LinearLayout errorBlock;
    private TextView errorMessage;

    private RequestQueue requestQueue;
    private String baseUrl;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        prefs = getSharedPreferences("TokenStore", MODE_PRIVATE);
        baseUrl = BuildConfig.BASE_URL;
        requestQueue = Volley.newRequestQueue(this);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        btnLogin = findViewById(R.id.btnLogin);
        errorBlock = findViewById(R.id.errorBlock);
        errorMessage = findViewById(R.id.errorMessage);
        btnForgot = findViewById(R.id.btnforgotPassWord);
        MaterialTextView registerHere = findViewById(R.id.registerHere);

        // ---------------- Ẩn lỗi khi người dùng nhập lại ----------------
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nếu ô email có nhập, gỡ lỗi riêng ô email
                if (!s.toString().trim().isEmpty()) {
                    layoutEmail.setErrorEnabled(false);
                    layoutEmail.setError(null);
                }
                // Nếu cả hai ô có dữ liệu, ẩn block lỗi chung
                if (!editTextEmail.getText().toString().trim().isEmpty()
                        || !editTextPassword.getText().toString().trim().isEmpty()) {
                    errorBlock.setVisibility(View.GONE);
                }
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nếu ô password có nhập, gỡ lỗi riêng ô password
                if (!s.toString().trim().isEmpty()) {
                    layoutPassword.setErrorEnabled(false);
                    layoutPassword.setError(null);
                }
                // Nếu cả hai ô có dữ liệu, ẩn block lỗi chung
                if (!editTextEmail.getText().toString().trim().isEmpty()
                        && !editTextPassword.getText().toString().trim().isEmpty()) {
                    errorBlock.setVisibility(View.GONE);
                }
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        // ---------------- Sự kiện nút bấm ----------------
        btnLogin.setOnClickListener(v -> signInUser());

        registerHere.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, Register.class)));

        btnForgot.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ForgotPassword.class)));
    }

    private void signInUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        boolean isValid = true;

        layoutEmail.setErrorEnabled(false);
        layoutPassword.setErrorEnabled(false);
        errorBlock.setVisibility(View.GONE);

        if (email.isEmpty()) {
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Vui lòng nhập email");
            isValid = false;
        }
        if (password.isEmpty()) {
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        }

        if (!isValid) return;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = baseUrl + "/auth/signin";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    try {
                        String accessToken = response.getString("accessToken");
                        String refreshToken = response.getString("refreshToken");
                        long expiresIn = response.getLong("accessExpiresIn");
                        long expiryTime = System.currentTimeMillis() + expiresIn;

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("access_token", accessToken);
                        editor.putString("refresh_token", refreshToken);
                        editor.putLong("access_expiry", expiryTime);
                        editor.apply();

                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        int status = error.networkResponse.statusCode;
                        String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Log.e("LoginError", "Status: " + status + ", Response: " + body);

                        if (status == 401 || status == 403) {
                            showError("Sai email hoặc mật khẩu");
                        } else {
                            showError("Lỗi máy chủ: " + status);
                        }
                    } else {
                        showError("Không thể kết nối đến máy chủ!");
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorBlock.setVisibility(View.VISIBLE);
    }
}
