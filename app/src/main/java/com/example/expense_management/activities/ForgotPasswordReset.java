package com.example.expense_management.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expense_management.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class ForgotPasswordReset extends AppCompatActivity {

    private TextInputEditText editTextPasswordNew, editTextPasswordNewCf;
    private MaterialButton btnReset;
    private MaterialTextView resetLoginHere;
    private String email; // Nhận từ màn OTP (nếu có)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_reset);

        // Ánh xạ View
        editTextPasswordNew = findViewById(R.id.editTextPasswordNew);
        editTextPasswordNewCf = findViewById(R.id.editTextPasswordNewCf);
        btnReset = findViewById(R.id.btnReset);
        resetLoginHere = findViewById(R.id.resetloginHere);

        // Lấy email
        email = getIntent().getStringExtra("email");

        // Nút Xác nhận
        btnReset.setOnClickListener(v -> resetPassword());

        // Quay lại đăng nhập
        resetLoginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordReset.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void resetPassword() {
        String newPassword = editTextPasswordNew.getText().toString().trim();
        String confirmPassword = editTextPasswordNewCf.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Xác nhận mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Giả lập cập nhật mật khẩu thành công
        Toast.makeText(this, "Đặt lại mật khẩu thành công! Vui lòng quay lại đăng nhập.", Toast.LENGTH_LONG).show();
    }
}
