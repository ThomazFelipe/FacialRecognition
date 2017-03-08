package bests.pi.facialrecognition.Resources;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import bests.pi.facialrecognition.*;
import bests.pi.facialrecognition.Domain.User;
import bests.pi.facialrecognition.FinalVariables.ImutableStrings;
import bests.pi.facialrecognition.Network.Controller;
import bests.pi.facialrecognition.Network.RequestLogin;
import bests.pi.facialrecognition.Validations.ValidField;

public class Login extends AppCompatActivity implements View.OnClickListener{

    protected EditText editTextEmail, editTextPassword;
    protected TextInputLayout layoutEmail, layoutPassword;
    protected Button buttonLogin;
    protected Toolbar toolbarLogin;
    protected ArrayList<EditText> arrayEditText = new ArrayList<>();
    protected ArrayList<TextInputLayout> arrayLayout = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();

        this.buttonLogin.setOnClickListener(this);
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
    public void onClick(View view) {
        boolean empty = false;
        for(int i = 0; i < arrayEditText.size(); i++){
            if(this.arrayEditText.get(i).getText().toString().isEmpty()){
                empty = true;
                this.arrayEditText.get(i).setError("Este campo não pode estar em branco!");
            }
        }
        if(!empty){
            if(ValidField.isValidEmail(this.editTextEmail)){
                if(ValidField.isCorrectPassword(this.editTextPassword)){
                    final Gson gson = new Gson();
                    RequestLogin request = new RequestLogin(Request.Method.POST, ImutableStrings.URL_LOGIN + editTextEmail.getText().toString().trim() + "/" + editTextPassword.getText().toString().trim(),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    try {
                                        User user = gson.fromJson( jsonObject.toString(), User.class );

                                        SharedPreferences sharedPreferences = getSharedPreferences(ImutableStrings.PREF_NAME,MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("id",user.getId().toString());
                                        editor.putString("email",user.getEmail());
                                        editor.putString("password",user.getPassword());
                                        editor.commit();
                                        Log.d("Response", gson.toString());

                                        Intent it = new Intent(Login.this, IsConnected.class);
                                        it.putExtra("user", (Parcelable) user);
                                        android.support.design.widget.Snackbar.make(editTextEmail, "Login Realizado com sucesso", 3000).show();
                                        startActivity(it);
                                        finish();

                                    }catch(Exception e)
                                    {
                                        Log.i("Log", "JsonException: "+e.getMessage());
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Login.this, "Senha e/ou E-mail incorretos!", Toast.LENGTH_SHORT);
                        }
                    }) {
                        @Override
                        public byte[] getBody() {
                            StringBuilder sb = new StringBuilder();
                            sb.append("{");
                            sb.append("\"").append(ImutableStrings.EMAIL).append("\":\"").append(editTextEmail.getText().toString().trim()).append("\",");
                            sb.append("\"").append(ImutableStrings.PASSWORD).append("\":\"").append(editTextPassword.getText().toString().trim()).append("\"");
                            sb.append("}");

                            return sb.toString().getBytes();
                        }
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }
                    };
                    Controller.getInstance(Login.this).addToRequestQuee(request);
                }
            }
        }
    }
    private void initialize() {
        this.editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        this.editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        this.layoutEmail = (TextInputLayout) findViewById(R.id.layoutEmail);
        this.layoutPassword = (TextInputLayout) findViewById(R.id.layoutPassword);
        this.buttonLogin = (Button) findViewById(R.id.buttonLogin);
        this.toolbarLogin = (Toolbar) findViewById(R.id.toolBarLogin);
        this.toolbarLogin.setTitle("Login");
        setSupportActionBar(this.toolbarLogin);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.arrayEditText.add(this.editTextEmail);
        this.arrayEditText.add(this.editTextPassword);
        this.arrayLayout.add(this.layoutEmail);
        this.arrayLayout.add(this.layoutPassword);
    }
}
