package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.nhn.android.naverlogin.OAuthLogin;

public class HomeActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    private static OAuthLogin mOAuthLoginInstance;

    int kakaoLogin = 1;
    int googleLogin = 2;
    int naverLongin = 3;
    int loginStatus;
    TextView nicknameText;
    SharedPreferences sf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        nicknameText = findViewById(R.id.nickname);
        Button logoutButton = findViewById(R.id.logout);
        //소셜 로그인 방법에 따라서 닉네임이나 프로필사진과 같은 데이터를 다르게 해주기 위해 쉐어드프리퍼런스로 구분된 숫자를 저장했다.
        //카카오는 1 구글은 2로 저장했다. 이 구분된 숫자를 사용해서 사용자가 소셜로그인을 한 플랫폼마다 다른 처리를 조건문으로 해주었다.
        sf = getSharedPreferences("logindata",MODE_PRIVATE);
        loginStatus = sf.getInt("loginStatus",0);
        setUserDataOnUI();

        logoutButton.setOnClickListener(v ->  {
            if(loginStatus == kakaoLogin){
                kakaoLogout();
            }
            else if (loginStatus == googleLogin) {
                googleLogout();
            }
            else if (loginStatus == naverLongin) {
                naverLogout();
            }
        });

    }

    void setUserDataOnUI() {
        //카카오 로그인을 했을 경우에 닉네임을 설정하는 부분
        if(loginStatus == kakaoLogin){
            String nickname =  sf.getString("kakaoNickname","it's null");
            Log.i("카카오 로그인 한 뒤에 닉네임 변경한 값", nickname);
            nicknameText.setText(nickname);
        }

        //구글 로그인을 했을 경우에 닉네임을 설정하는 부분
        else if (loginStatus == googleLogin) {
            //구글 로그인을 했을때 없으면 에러나는 코드인데 뭔지 잘 모르겠다
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
            if (acct != null) {
                String personName = acct.getDisplayName();
                Uri personPhoto = acct.getPhotoUrl();
                Log.i("구글로그인 한 뒤에 닉네임 변경한 값", personName);
                nicknameText.setText(personName);
            }
        }

        else if (loginStatus == naverLongin) {
            mOAuthLoginInstance = OAuthLogin.getInstance();
            nicknameText.setText(sf.getString("naverNickname","it's null"));
        }
    }

    private void googleLogout() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "구글 로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
    private void kakaoLogout() {
        Toast.makeText(getApplicationContext(), "카카오 로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(logoutIntent);
            }
        });
    }
    private void naverLogout() {
        Toast.makeText(getApplicationContext(), "네이버 로그아웃 되었습니다", Toast.LENGTH_SHORT).show();

        mOAuthLoginInstance.logout(getApplicationContext());
        mOAuthLoginInstance.logoutAndDeleteToken(getApplicationContext());
        Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(logoutIntent);
    }

}
