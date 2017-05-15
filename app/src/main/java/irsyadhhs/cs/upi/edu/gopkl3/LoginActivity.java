package irsyadhhs.cs.upi.edu.gopkl3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import butterknife.ButterKnife;
import butterknife.Bind;

import static irsyadhhs.cs.upi.edu.gopkl3.AppConfig.SELECT_PEMBELI;
import static irsyadhhs.cs.upi.edu.gopkl3.AppConfig.SP;
import static irsyadhhs.cs.upi.edu.gopkl3.AppConfig.TAG;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sp = getSharedPreferences(SP, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        if(sp.getString("st","GAGAL").equals("active")){
            Intent in = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(in);
            finish();
        }

        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                View view = LoginActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(LoginActivity.this, RegisterAct.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        checkExist(username, password);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    void checkExist(final String un, final  String pw) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, SELECT_PEMBELI, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i(TAG, "onResponse: playerResult= " + response.toString());
                        String name;
                        String pass;
                        int stop = 0;
                        try {
                            JSONArray jsonArray = response.getJSONArray("users");

                            if (response.getString("success").equalsIgnoreCase("1")) {
                                int i;
                                i = 0;
                                while ((i < jsonArray.length()) && (stop == 0)) {
                                    JSONObject pembeli = jsonArray.getJSONObject(i);
                                    name = pembeli.getString("nama");
                                    pass = pembeli.getString("pass");
                                    if (name.equals(un.toString())) {
                                        stop = 1;
                                        if(pass.equals(pw.toString())){
                                            SharedPreferences sp = getSharedPreferences(SP, MODE_PRIVATE);
                                            SharedPreferences.Editor ed = sp.edit();
                                            ed.putString("un", un);
                                            ed.putString("pw", pw);
                                            ed.putString("st","active");
                                            ed.commit();

                                            Toast.makeText(LoginActivity.this, "Log in successful", Toast.LENGTH_SHORT).show();
                                            Intent in = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(in);
                                            finish();
                                        }else{
                                            Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    i++;
                                }

                                if (stop == 0) {
                                    Toast.makeText(LoginActivity.this, "Username doesn't exist", Toast.LENGTH_SHORT).show();
                                }else{

                                }
                            } else if (response.getString("success").equalsIgnoreCase("0")) {

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menampilkan error pada logcat
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());

                    }
                }
        );

        AppController.getInstance().addToRequestQueue(request);
    }

    /*@Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }*/

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        //finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        /*if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }*/

        if (password.isEmpty() || password.length() < 4 || password.length() > 16) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}

