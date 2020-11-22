package com.example.yehwankim23.streetfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.button_reset_password).setOnClickListener(onClickListener);
        findViewById(R.id.button_cancel).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_reset_password:
                    resetPassword();
                    break;
                case R.id.button_cancel:
                    onBackPressed();
                    break;
            }
        }
    };

    private void resetPassword() {
        String email = ((EditText) findViewById(R.id.text_email)).getText().toString();

        if (email.length() > 0) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showToast("비밀번호 재설정 이메일을 보냈습니다.");
                                startAnActivity(SignInActivity.class);
                            } else {
                                if (task.getException() != null) {
                                    showToast(task.getException().toString());
                                }
                            }
                        }
                    });
        } else {
            showToast("이메일을 입력하세요.");
        }
    }

    private void startAnActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
