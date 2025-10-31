package com.example.expense_management.activities;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.expense_management.BuildConfig;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.example.expense_management.R;
import com.example.expense_management.BuildConfig;



import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class RegisterOTP extends AppCompatActivity {

    private TextInputEditText editTextOTP;
    private MaterialButton btnContinueOTP;
    private MaterialTextView tvResendOtp;

    private RequestQueue requestQueue;
    private String email;
    private String baseUrl;
    TextInputLayout layoutOtp;
    TextInputEditText editTextOtp;
    private boolean canResend = false;
    private CountDownTimer countDownTimer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        baseUrl = BuildConfig.BASE_URL;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_otp);

        // Lấy email được truyền từ màn hình Đăng ký
        email = getIntent().getStringExtra("email");
        requestQueue = Volley.newRequestQueue(this);

        editTextOTP = findViewById(R.id.editTextOTP);
        btnContinueOTP = findViewById(R.id.btnContinueOTP);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        layoutOtp = findViewById(R.id.layoutotp);
        editTextOtp = findViewById(R.id.editTextOTP);

        startOtpCountdown();
        btnContinueOTP.setOnClickListener(v -> verifyOtp());
        editTextOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearError(layoutOtp);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        tvResendOtp.setOnClickListener(v -> {
            startOtpCountdown();
            clearError(layoutOtp); // reset lỗi cũ
            String otp = editTextOtp.getText().toString().trim();

            boolean isValid = true;

            if (otp.isEmpty()) {
                setError(layoutOtp, "Vui lòng nhập mã OTP");
                isValid = false;
            }

            if (!isValid) return; // không gửi request nếu lỗi

            resendOtp();
        });
    }

    private void setError(TextInputLayout layout, String message) {
        layout.setErrorEnabled(true);
        layout.setError(message);
    }

    private void clearError(TextInputLayout layout) {
        layout.setErrorEnabled(false);
        layout.setError(null);
    }

    private void verifyOtp() {
        String otp = editTextOTP.getText().toString().trim();

        if (otp.isEmpty()) {
            setError(layoutOtp, "Vui lòng nhập mã OTP");
            return;
        }

        String url = baseUrl + "/auth/verify";
        JSONObject body = new JSONObject();

        try {
            body.put("email", email);
            body.put("verificationCode", otp);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.d("VerifyOTP", "POST " + url + " → " + body);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response -> {
                    try {
                        String message = response.getString("message");

                        if (message.equals("Account verified successfully")) {
                            showSuccessDialog(); // <-- gọi popup custom
                        } else {
                            setError(layoutOtp, "Mã OTP không đúng");
                        }

                    } catch (JSONException e) {
                        Log.e("VerifyError", "JSON parse error: " + e.getMessage());
                        Toast.makeText(this, "Lỗi xử lý phản hồi từ máy chủ!", Toast.LENGTH_LONG).show();
                    }
                },

                error -> {
                    // Khi có lỗi (backend trả RuntimeException)
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Log.e("VerifyError", "Response: " + responseBody);

                        if (responseBody.contains("Invalid verification code")) {
                            setError(layoutOtp, "Mã OTP không đúng");
                        } else if (responseBody.contains("Verification code expired")) {
                            setError(layoutOtp, "Mã OTP đã hết hạn");
                        } else if (responseBody.contains("User not found")) {
                            setError(layoutOtp, "Không tìm thấy tài khoản này");
                        } else {
                            Toast.makeText(this, "❌ " + responseBody, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e("VerifyError", "Error: " + error.toString());
                        Toast.makeText(this, "Không thể kết nối máy chủ!", Toast.LENGTH_LONG).show();
                    }
                }
        );


        requestQueue.add(request);
    }

    private void resendOtp() {
        String url = baseUrl + "/auth/resend?email=" + email;
        Log.d("ResendOTP", "POST " + url);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                response -> {
                    Toast.makeText(this, "Đã gửi lại mã OTP đến " + email, Toast.LENGTH_LONG).show();
                },
                error -> {
                    String errorMsg = "Không thể gửi lại mã OTP!";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Log.e("ResendError", "Response: " + responseBody);
                        errorMsg = responseBody;
                    }
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                });

        requestQueue.add(request);
    }

    private void startOtpCountdown() {
        tvResendOtp.setEnabled(false);
        canResend = false;

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvResendOtp.setText("Gửi lại mã OTP (" + (millisUntilFinished / 1000) + "s)");
            }

            @Override
            public void onFinish() {
                tvResendOtp.setText("Gửi lại mã OTP");
                tvResendOtp.setEnabled(true);
                canResend = true;
            }
        }.start();
    }

    private void showSuccessDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_signup_success, null);
        builder.setView(dialogView);

        android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().setDimAmount(0.6f); // Độ mờ nền, 0f = trong suốt, 1f = rất mờ
        }


        dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.75),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );


        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        params.y = -150; //
        dialog.getWindow().setAttributes(params);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;

        Button btnContinue = dialogView.findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(RegisterOTP.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        dialog.setCanceledOnTouchOutside(true);
    }

}

