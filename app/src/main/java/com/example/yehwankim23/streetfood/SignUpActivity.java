package com.example.yehwankim23.streetfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.button_sign_up).setOnClickListener(onClickListener);
        findViewById(R.id.button_goto_sign_in).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_sign_up:
                    signUp();
                    break;
                case R.id.button_goto_sign_in:
                    onBackPressed();
                    break;
            }
        }
    };

    private void signUp() {
        final String email = ((EditText) findViewById(R.id.text_email)).getText().toString();
        final String password = ((EditText) findViewById(R.id.text_password)).getText().toString();
        String passwordConfirm = ((EditText) findViewById(R.id.text_password_confirm)).getText().toString();

        if (email.length() > 0 && password.length() > 0 && passwordConfirm.length() > 0) {
            if (password.equals(passwordConfirm)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if (currentUser != null) {
                                        User user = new User(email, password);
                                        db.collection("user").document(currentUser.getUid())
                                                .set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        currentUser.sendEmailVerification()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            showToast("이메일 주소 인증 이메일을 보냈습니다.");
                                                                            startAnActivity(SignInActivity.class);
                                                                        } else {
                                                                            if (task.getException() != null) {
                                                                                showToast(task.getException().toString());
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        showToast(e.getMessage());
                                                    }
                                                });
                                    }
                                } else {
                                    if (task.getException() != null) {
                                        showToast(task.getException().toString());
                                    }
                                }
                            }
                        });
            } else {
                showToast("비밀번호가 일치하지 않습니다.");
            }
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
