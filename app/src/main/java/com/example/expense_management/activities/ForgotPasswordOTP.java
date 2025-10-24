package com.example.expense_management.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expense_management.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class ForgotPasswordOTP extends AppCompatActivity {

    private TextInputEditText editTextOtp;
    private MaterialButton btnVerifyOtp;
    private MaterialTextView resendOtpText;

    private String email; // Nhận email từ màn ForgotPasswordEmailActivity
    private boolean canResend = false;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_otp);

        // Ánh xạ view
        editTextOtp = findViewById(R.id.editTextOTP);
        btnVerifyOtp = findViewById(R.id.btnContinueOTP);
        resendOtpText = findViewById(R.id.tvResendOtp);

        // Nhận email được truyền sang
        email = getIntent().getStringExtra("email");

        // Bắt đầu đếm ngược gửi lại OTP
        startOtpCountdown();

        // Khi người dùng bấm xác nhận OTP
        btnVerifyOtp.setOnClickListener(v -> verifyOtp());

        // Khi người dùng bấm “Gửi lại mã OTP”
        resendOtpText.setOnClickListener(v -> {
            if (canResend) {
                resendOtp();
            } else {
                Toast.makeText(this, "Vui lòng chờ đến khi hết thời gian đếm ngược!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Kiểm tra OTP người dùng nhập
     */
    private void verifyOtp() {
        String otp = editTextOtp.getText().toString().trim();

        if (TextUtils.isEmpty(otp)) {
            Toast.makeText(this, "Vui lòng nhập mã OTP!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (otp.length() < 4) {
            Toast.makeText(this, "Mã OTP phải có ít nhất 4 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 👉 Giả lập xác minh OTP (bạn có thể thay bằng gọi API thực)
        if (otp.equals("1234")) {
            Toast.makeText(this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();

            // Chuyển sang bước đặt lại mật khẩu
            Intent intent = new Intent(ForgotPasswordOTP.this, ForgotPasswordReset.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Mã OTP không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Hàm giả lập gửi lại mã OTP
     */
    private void resendOtp() {
        Toast.makeText(this, "Đã gửi lại mã OTP đến " + email, Toast.LENGTH_LONG).show();
        canResend = false;
        startOtpCountdown();
    }

    /**
     * Đếm ngược 60 giây cho chức năng gửi lại OTP
     */
    private void startOtpCountdown() {
        resendOtpText.setEnabled(false);
        canResend = false;

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendOtpText.setText("Gửi lại mã OTP (" + (millisUntilFinished / 1000) + "s)");
            }

            @Override
            public void onFinish() {
                resendOtpText.setText("Gửi lại mã OTP");
                resendOtpText.setEnabled(true);
                canResend = true;
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
