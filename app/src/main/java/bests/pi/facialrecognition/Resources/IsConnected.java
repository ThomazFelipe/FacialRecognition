package bests.pi.facialrecognition.Resources;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import bests.pi.facialrecognition.R;

public class IsConnected extends AppCompatActivity implements View.OnClickListener{

    protected Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_is_connected);

        this.buttonLogout = (Button) findViewById(R.id.buttonLogout);
        this.buttonLogout.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        startActivity(new Intent(IsConnected.this, HomeScreen.class));
        finish();
    }
}
