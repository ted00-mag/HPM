package si.uni_lj.fe.tnuv.hpm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    ZXingScannerView zXingScannerView;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zXingScannerView = new ZXingScannerView(this);
        setContentView(zXingScannerView);

        mFirebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void handleResult(Result result) {

        //LoginActivity.textFromQRCode = result.getText();
        //Log.e("handler", result.getText()); // Prints scan results
        //Log.e("handler", result.getBarcodeFormat().toString()); // Prints the scan format (qrcode)
        // Assigning the required vales from result *************
        String[] arrayOfResultString = new String[1];
        arrayOfResultString[0] = result.getText();
        String[] arrayOfResultStringDOJ = arrayOfResultString[0].split(" ");
        Log.e("handler", arrayOfResultStringDOJ[0]); // Prints scan results
        Log.e("handler", arrayOfResultStringDOJ[1]); // Prints scan results


        String email = arrayOfResultStringDOJ[0];
        String pwd = arrayOfResultStringDOJ[1];

        mFirebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(ScanQRActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(ScanQRActivity.this, "Login Error, Please login again!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent inttoHome = new Intent(ScanQRActivity.this, HOmeActivity.class);
                    startActivity(inttoHome);
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }
}
