package com.example.expense_management.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expense_management.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class ForgotPassword extends AppCompatActivity {

    private TextInputEditText editTextEmail;
    private MaterialButton btnContinue;
    private MaterialTextView loginHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        // Ánh xạ view
        editTextEmail = findViewById(R.id.editTextEmailForgot);
        btnContinue = findViewById(R.id.btnContinue);
        loginHere = findViewById(R.id.forgotloginHere);

        // Khi nhấn “Tiếp tục”
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();

                // Kiểm tra định dạng email
                if (email.isEmpty()) {
                    Toast.makeText(ForgotPassword.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ForgotPassword.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gửi request API lấy OTP ở đây (giả lập hoặc thật)
                sendOtpRequest(email);

            }
        });

        // Khi nhấn “Đăng nhập”
        loginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Hàm mô phỏng gửi OTP đến email
     * Thực tế sẽ gọi API từ backend
     */
    private void sendOtpRequest(String email) {
        // Tạm thời giả lập việc gửi thành công
        Toast.makeText(this, "Mã OTP đã được gửi đến " + email, Toast.LENGTH_LONG).show();

        // Sau khi gửi OTP thành công, chuyển sang màn hình nhập OTP
        Intent intent = new Intent(ForgotPassword.this, ForgotPasswordOTP.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }
}
