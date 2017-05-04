package bests.pi.facialrecognition.Resources;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

    private Toolbar toolbarRegistration;
    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private TextInputLayout layoutEmail, layoutPassword, layoutConfirmPassword;
    private Button buttonRegistration;
    private Button buttonCamera;
    private ArrayList<EditText> arrayEditText = new ArrayList<>();
    private ArrayList<TextInputLayout> arrayLayout = new ArrayList<>();
    private int cont;
    private String [] allImages = new String[4];
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        inicialize();
        this.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();
                Drawable image = new BitmapDrawable(BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length));
                imageView.setImageDrawable(image);
            }
        });
        this.buttonRegistration.setOnClickListener(this);
        this.buttonCamera.setOnClickListener(this);
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
                    if (ValidField.isEqualsPasswords(this.editTextPassword,
                            this.editTextConfirmPassword)) {
                        if(cont > 0) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                    ImutableVariables.URL_REGISTRATION,
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
                                    android.support.design.widget.Snackbar.make(view,
                                            "Erro ao cadastrar, verifique sua conexão", 3000).show();
                                    error.printStackTrace();
                                }
                            }) {
                                @Override
                                public byte[] getBody() {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("{");
                                    sb.append("\"").append(ImutableVariables.EMAIL).append("\":\"")
                                            .append(editTextEmail.getText().toString().trim()).append("\",");
                                    sb.append("\"").append(ImutableVariables.PASSWORD).append("\":\"")
                                            .append(editTextPassword.getText().toString().trim()).append("\",");
                                    sb.append("\"").append(ImutableVariables.IMAGE).append("\":\"")
                                            .append(allImages[0].trim()).append("\"");
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
                            android.support.design.widget.Snackbar.make(view,
                                    "Desculpe, precisamos de suas fotos! Tire-as e tente novamente", 3000)
                                    .show();
                        }
                    }
                }
            }
        }
        else{
            cont = 0;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                 if (verifyPermission()){
                    //while (cont < 4) {
                        dispatchTakePictureIntent();
                        //++cont;
                    //}
                }
            }
            else {
                //while (cont < 4) {
                dispatchTakePictureIntent();
                //}
                buttonCamera.setEnabled(false);

            }
        }
    }
    private boolean verifyPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(Registration.this, Manifest.permission.CAMERA);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        ActivityCompat.requestPermissions(Registration.this, new String[]{Manifest.permission.CAMERA}, 101);
        return verifyPermission();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImutableVariables.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            assert imageBitmap != null;
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            allImages[cont] = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
            cont++;
        }
    }
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
        this.buttonCamera = (Button) findViewById(R.id.buttonCamera);
        this.imageView = (ImageView) findViewById(R.id.imageRegistration);
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
