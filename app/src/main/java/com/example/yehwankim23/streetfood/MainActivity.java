package com.example.yehwankim23.streetfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.button_goto_update_user_info).setOnClickListener(onClickListener);
        findViewById(R.id.button_goto_sign_out).setOnClickListener(onClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startAnActivity(SignInActivity.class);
        } else {
            if (!currentUser.isEmailVerified()) {
                mAuth.signOut();
                showToast("이메일을 인증하세요.");
                startAnActivity(SignInActivity.class);
            } else {
                DocumentReference docRef = db.collection("user").document(currentUser.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {
                                    if (document.getData() != null) {
                                        if (Objects.equals(document.getData().get("username"), "")) {
                                            showToast("회원정보를 입력하세요.");
                                            startAnActivity(UpdateUserInfoActivity.class);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (task.getException() != null) {
                                showToast(task.getException().toString());
                            }
                        }
                    }
                });
            }
        }
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
                case R.id.button_goto_update_user_info:
                    startAnActivity(UpdateUserInfoActivity.class);
                    break;
                case R.id.button_goto_sign_out:
                    mAuth.signOut();
                    showToast("로그아웃에 성공했습니다.");
                    startAnActivity(SignInActivity.class);
                    break;
            }
        }
    };

    private void startAnActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
