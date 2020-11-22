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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.button_sign_in).setOnClickListener(onClickListener);
        findViewById(R.id.button_goto_sign_up).setOnClickListener(onClickListener);
        findViewById(R.id.button_goto_reset_password).setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_sign_in:
                    signIn();
                    break;
                case R.id.button_goto_sign_up:
                    startAnActivity(SignUpActivity.class);
                    break;
                case R.id.button_goto_reset_password:
                    startAnActivity(ResetPasswordActivity.class);
                    break;
            }
        }
    };

    private void signIn() {
        String email = ((EditText) findViewById(R.id.text_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.text_password)).getText().toString();

        if (email.length() > 0 && password.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if (currentUser != null) {
                                    if (currentUser.isEmailVerified()) {
                                        showToast("로그인에 성공했습니다.");
                                        startAnActivity(MainActivity.class);
                                    } else {
                                        mAuth.signOut();
                                        showToast("이메일을 인증하세요.");
                                    }
                                }
                            } else {
                                if (task.getException() != null) {
                                    showToast(task.getException().toString());
                                }
                            }
                        }
                    });
        } else {
            showToast("이메일 또는 비밀번호를 입력하세요.");
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
