package com.example.expense_management.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.expense_management.BuildConfig;
import com.example.expense_management.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Register extends AppCompatActivity {

    private TextInputEditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword, dateOfBirthInput;
    private AutoCompleteTextView genderDropdown;
    private MaterialButton btnRegister;
    private TextInputLayout layoutEmail, layoutPassword, layoutConfirm, layoutUsername, layoutDob, layoutGender;
    private RequestQueue requestQueue;
    private String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register);

        baseUrl = BuildConfig.BASE_URL;

        // Ánh xạ view
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirm_Password);
        genderDropdown = findViewById(R.id.genderDropdown);
        dateOfBirthInput = findViewById(R.id.dateOfBirthInput);
        btnRegister = findViewById(R.id.btnSignup);
        MaterialTextView loginHere = findViewById(R.id.loginHere);

        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        layoutConfirm = findViewById(R.id.layoutConfirmPassword);
        layoutUsername = findViewById(R.id.layoutName);
        layoutDob = findViewById(R.id.layoutDob);
        layoutGender = findViewById(R.id.layoutGender);

        requestQueue = Volley.newRequestQueue(this);

        // Dropdown giới tính
        String[] genders = new String[]{"Nam", "Nữ"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        genderDropdown.setAdapter(adapter);

        // Date picker
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày sinh")
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setEnd(MaterialDatePicker.todayInUtcMilliseconds())
                        .build())
                .build();

        dateOfBirthInput.setOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "DATE_PICKER"));
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateOfBirthInput.setText(sdf.format(new Date(selection)));
            clearError(layoutDob);
        });

        genderDropdown.setOnItemClickListener((parent, view, position, id) -> clearError(layoutGender));

        // Khi người dùng nhập lại, tự động ẩn lỗi
        editTextName.addTextChangedListener(new SimpleTextWatcher(() -> clearError(layoutUsername)));
        editTextEmail.addTextChangedListener(new SimpleTextWatcher(() -> clearError(layoutEmail)));
        editTextPassword.addTextChangedListener(new SimpleTextWatcher(() -> clearError(layoutPassword)));
        editTextConfirmPassword.addTextChangedListener(new SimpleTextWatcher(() -> clearError(layoutConfirm)));
        dateOfBirthInput.addTextChangedListener(new SimpleTextWatcher(() -> clearError(layoutDob)));

        btnRegister.setOnClickListener(v -> signupUser());
        loginHere.setOnClickListener(v -> startActivity(new Intent(Register.this, MainActivity.class)));
    }

    private void signupUser() {
        String username = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String dob = dateOfBirthInput.getText().toString().trim();
        String gender = genderDropdown.getText().toString().trim();

        // Reset lỗi
        clearError(layoutUsername);
        clearError(layoutEmail);
        clearError(layoutPassword);
        clearError(layoutConfirm);
        clearError(layoutDob);
        clearError(layoutGender);

        boolean isValid = true;

        if (username.isEmpty()) {
            setError(layoutUsername, "Tên không được để trống");
            isValid = false;
        }
        if (email.isEmpty()) {
            setError(layoutEmail, "Email không được để trống");
            isValid = false;
        }
        if (password.isEmpty()) {
            setError(layoutPassword, "Mật khẩu không được để trống");
            isValid = false;
        }
        if (confirmPassword.isEmpty()) {
            setError(layoutConfirm, "Vui lòng nhập lại mật khẩu");
            isValid = false;
        }
        if (!password.equals(confirmPassword)) {
            setError(layoutConfirm, "Mật khẩu xác nhận không khớp");
            isValid = false;
        }
        if (dob.isEmpty()) {
            setError(layoutDob, "Vui lòng nhập ngày sinh");
            isValid = false;
        }
        if (gender.isEmpty()) {
            setError(layoutGender, "Vui lòng chọn giới tính");
            isValid = false;
        }

        if (!isValid) return;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("fullName", username);
            requestBody.put("email", email);
            requestBody.put("password", password);
            requestBody.put("birthDay", dob);
            requestBody.put("gender", gender);
        } catch (JSONException e) {
            Toast.makeText(this, "Lỗi tạo dữ liệu JSON!", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = baseUrl + "/auth/signup";
        Log.d("Signup", "POST " + url + " → " + requestBody);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    showTopToast("Đăng ký thành công!", true);
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Log.e("RegisterError", "Response: " + responseBody);
                    }
                    Toast.makeText(this, "Lỗi khi đăng ký!", Toast.LENGTH_LONG).show();
                });

        requestQueue.add(request);
    }

    // Ẩn lỗi hoàn toàn
    private void clearError(TextInputLayout layout) {
        layout.setError(null);
        layout.setErrorEnabled(false);
    }

    // Hiển thị lỗi
    private void setError(TextInputLayout layout, String message) {
        layout.setErrorEnabled(true);
        layout.setError(message);
    }

    private static class SimpleTextWatcher implements TextWatcher {
        private final Runnable afterTextChanged;

        public SimpleTextWatcher(Runnable afterTextChanged) {
            this.afterTextChanged = afterTextChanged;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) {
            afterTextChanged.run();
        }
    }

    private void showTopToast(String message, boolean isSuccess) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, findViewById(android.R.id.content), false);

        TextView toastText = layout.findViewById(R.id.toastMessage);
        ImageView toastIcon = layout.findViewById(R.id.toastIcon);
        LinearLayout toastRoot = layout.findViewById(R.id.toastRoot);

        toastText.setText(message);
        if (isSuccess) {
            toastRoot.setBackgroundResource(R.drawable.bg_toast_success);
            toastIcon.setImageResource(R.drawable.ic_check);
        }

        // Animation trượt xuống
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        layout.startAnimation(slideDown);

        // Tạo Toast tùy chỉnh
        Toast toast = new Toast(getApplicationContext());
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
        toast.show();
    }

}
