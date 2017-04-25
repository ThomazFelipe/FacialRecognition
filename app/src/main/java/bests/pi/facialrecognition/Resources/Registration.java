package bests.pi.facialrecognition.Resources;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import bests.pi.facialrecognition.*;
import bests.pi.facialrecognition.FinalVariables.ImutableVariables;
import bests.pi.facialrecognition.Network.Controller;
import bests.pi.facialrecognition.Validations.ValidField;

public class Registration extends AppCompatActivity implements View.OnClickListener{

    protected Toolbar toolbarRegistration;
    protected EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    protected TextInputLayout layoutEmail, layoutPassword, layoutConfirmPassword;
    protected Button buttonRegistration;
    protected FloatingActionButton floatingButtonCamera;
    protected ArrayList<EditText> arrayEditText = new ArrayList<>();
    protected ArrayList<TextInputLayout> arrayLayout = new ArrayList<>();
    protected int cont, i;
    protected byte [] picture = new byte[4];
    protected String [] allImages = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        inicialize();

        this.buttonRegistration.setOnClickListener(this);
        this.floatingButtonCamera.setOnClickListener(this);
    }
    @Override
    public void onClick(final View view) {

        if(view == buttonRegistration) {
            boolean empty = false;
            for (int i = 0; i < arrayEditText.size(); i++) {
                if (this.arrayEditText.get(i).getText().toString().isEmpty()) {
                    empty = true;
                    this.arrayEditText.get(i).setError("Este campo não pode estar em branco!");
                }
            }
            if (!empty) {
                if (ValidField.isValidEmail(this.editTextEmail)) {
                    if (ValidField.isEqualsPasswords(this.editTextPassword, this.editTextConfirmPassword)) {
                        if(cont > 2) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, ImutableVariables.URL_REGISTRATION,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);
                                            builder.setTitle("Dados gravados com sucesso!");
                                            builder.setMessage(" Faça o login para continuar!");
                                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startActivity(new Intent(Registration.this, Login.class));
                                                    finish();
                                                }
                                            });
                                            builder.show();
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    android.support.design.widget.Snackbar.make(view, "Erro ao cadastrar, verifique sua conexão", 3000).show();
                                    error.printStackTrace();
                                }
                            }) {
                                @Override
                                public byte[] getBody() {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("{");
                                    sb.append("\"").append(ImutableVariables.EMAIL).append("\":\"").append(editTextEmail.getText().toString().trim()).append("\",");
                                    sb.append("\"").append(ImutableVariables.PASSWORD).append("\":\"").append(editTextPassword.getText().toString().trim()).append("\"");
                                    sb.append("\"").append(ImutableVariables.IMAGE).append("\":\"").append(allImages[0].toString().trim()).append("\"");
                                    sb.append("}");

                                    return sb.toString().getBytes();
                                }

                                @Override
                                public String getBodyContentType() {
                                    return "application/json; charset=utf-8";
                                }
                            };
                            Controller.getInstance(Registration.this).addToRequestQuee(stringRequest);
                        }
                        else{
                            android.support.design.widget.Snackbar.make(view, "Desculpe, precisamos de suas fotos! Tire-as e tente novamente", 3000).show();
                        }
                    }
                }
            }
        }
        else{
            cont = 0;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,}, 101);
                if (ActivityCompat.shouldShowRequestPermissionRationale(Registration.this,
                        Manifest.permission.CAMERA)) {
                    while (cont < 4) {
                        dispatchTakePictureIntent();
                    }
                }
            } else {
                while (cont < 4) {
                    dispatchTakePictureIntent();
                }
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            setIntent(new Intent(this, HomeScreen.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImutableVariables.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            assert imageBitmap != null;
            imageBitmap.compress(Bitmap.CompressFormat.PNG,100, byteArrayOutputStream);
            allImages[cont] = new String(byteArrayOutputStream.toByteArray());
            cont++;
        }
    }*/
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RESULT_OK: {
                cont++;
            }
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, ImutableVariables.REQUEST_IMAGE_CAPTURE);
        }
    }
    private void inicialize() {
        this.editTextEmail = (EditText) findViewById(R.id.editTextEmailRegistration);
        this.editTextPassword = (EditText) findViewById(R.id.editTextPasswordRegistration);
        this.editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPasswordRegistration);
        this.layoutEmail = (TextInputLayout) findViewById(R.id.layoutEmailRegistration);
        this.layoutPassword = (TextInputLayout) findViewById(R.id.layoutPasswordRegistration);
        this.layoutConfirmPassword = (TextInputLayout) findViewById(R.id.layoutConfirmPasswordRegistration);
        this.buttonRegistration = (Button) findViewById(R.id.buttonRegistration);
        this.toolbarRegistration = (Toolbar) findViewById(R.id.toolBarRegistration);
        this.floatingButtonCamera = (FloatingActionButton) findViewById(R.id.floatingButtonCamera);
        this.cont = 0;
        setSupportActionBar(this.toolbarRegistration);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.arrayEditText.add(this.editTextEmail);
        this.arrayEditText.add(this.editTextPassword);
        this.arrayEditText.add(this.editTextConfirmPassword);
        this.arrayLayout.add(this.layoutEmail);
        this.arrayLayout.add(this.layoutPassword);
        this.arrayLayout.add(this.layoutConfirmPassword);
    }

}
