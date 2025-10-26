package com.example.expense_management.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.expense_management.BuildConfig;
import com.example.expense_management.R;
import com.example.expense_management.api.ApiServices;
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

    private MaterialButton btnLogin;
    private RequestQueue requestQueue;
    private String baseUrl;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        prefs = getSharedPreferences("TokenStore", MODE_PRIVATE);
        MaterialTextView registerHere = findViewById(R.id.registerHere);
        baseUrl = BuildConfig.BASE_URL;
        String accessToken = prefs.getString("access_token", null);
        String refreshToken = prefs.getString("refresh_token", null);
        requestQueue = Volley.newRequestQueue(this);


        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });
        editTextEmail=findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);
        requestQueue = Volley.newRequestQueue(this);
        btnLogin=findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> signInUser() );
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
    }

    private void signInUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        boolean isValid = true;

        // Reset lỗi trước
        layoutEmail.setError(null);
        layoutPassword.setError(null);

        // Kiểm tra từng trường
        if (email.isEmpty()) {
            layoutEmail.setError("Vui lòng nhập email");
            isValid = false;
        }
        if (password.isEmpty()) {
            layoutPassword.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        }

        if (!isValid) return;

        // Tạo request body
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
                        prefs = getSharedPreferences("TokenStore", MODE_PRIVATE);
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
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        int status = error.networkResponse.statusCode;
                        String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Log.e("LoginError", "Status: " + status + ", Response: " + body);

                        if (status == 401 || status == 403) {
                            layoutPassword.setError("Sai email hoặc mật khẩu");
                        } else {
                            Toast.makeText(this, "Lỗi máy chủ: " + status, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Không thể kết nối đến máy chủ!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);

        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layoutEmail.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layoutPassword.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

}