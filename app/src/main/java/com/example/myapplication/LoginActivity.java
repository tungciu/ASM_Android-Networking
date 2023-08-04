package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText ed_user, ed_pass;
    Button btn_login, btn_dangky;

    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    TextView btn_forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        anhxaview();
        mAuth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy dữ liệu email và mật khẩu từ các EditText
                String email = ed_user.getText().toString().trim();
                String passWord = ed_pass.getText().toString().trim();
                // Gọi phương thức clickLogin để xử lý việc đăng nhập
                clickLogin(email, passWord);
            }
        });

        btn_dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chuyển người dùng đến RegisterrActivity bằng Intent
                startActivity(new Intent(LoginActivity.this, RegisterrActivity.class));
            }
        });



    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Nếu người dùng đã đăng nhập, chuyển đến MainActivity và kết thúc LoginActivity.
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void clickLogin(String email, String passWord) {
        // Phương thức để xử lý sự kiện khi người dùng bấm nút đăng nhập.
        mAuth.signInWithEmailAndPassword(email, passWord)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Nếu đăng nhập thành công, chuyển đến MainActivity và kết thúc LoginActivity.
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            finish();
                            startActivity(intent);
                            // Hiển thị thông báo "login success!" bằng Toast.
                            Toast.makeText(LoginActivity.this, "login success!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Nếu đăng nhập thất bại, hiển thị thông báo "Login failed" bằng Toast.
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void anhxaview(){
        btn_forgot=findViewById(R.id.forgot);
        progressDialog = new ProgressDialog(this);
        ed_user = findViewById(R.id.ed_email);
        ed_pass = findViewById(R.id.ed_password);
        btn_login = findViewById(R.id.btn_login);
        btn_dangky = findViewById(R.id.btn_dangky);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}