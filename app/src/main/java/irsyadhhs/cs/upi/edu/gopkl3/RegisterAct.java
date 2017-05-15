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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import butterknife.Bind;
import butterknife.ButterKnife;

import static irsyadhhs.cs.upi.edu.gopkl3.AppConfig.INSERT_PEMBELI;
import static irsyadhhs.cs.upi.edu.gopkl3.AppConfig.SELECT_PEMBELI;
import static irsyadhhs.cs.upi.edu.gopkl3.AppConfig.SP;
import static irsyadhhs.cs.upi.edu.gopkl3.AppConfig.TAG;

public class RegisterAct extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_name)
    EditText _nameText;
    @Bind(R.id.input_address) EditText _addressText;
    //@Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_mobile) EditText _mobileText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;
    String lasttime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        SharedPreferences sp = getSharedPreferences(SP, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        if(sp.getString("st","GAGAL").equals("active")){
            Intent in = new Intent(RegisterAct.this, MainActivity.class);
            startActivity(in);
            finish();
        }
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = RegisterAct.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterAct.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String address = _addressText.getText().toString();
        // String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        checkExist(name);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        //setResult(RESULT_OK, null);
        //finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String address = _addressText.getText().toString();
        //String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (address.isEmpty()) {
            _addressText.setError("Enter Valid Address");
            valid = false;
        } else {
            _addressText.setError(null);
        }


       /* if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }*/

        if (mobile.isEmpty() || mobile.length()<10 || mobile.length()>13) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 16) {
            _passwordText.setError("between 4 and 16 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 16 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

    void checkExist(final String un) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, SELECT_PEMBELI, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i(TAG, "onResponse: playerResult= " + response.toString());
                        String name;
                        int stop = 0;
                        try {
                            JSONArray jsonArray = response.getJSONArray("users");

                            if (response.getString("success").equalsIgnoreCase("1")) {
                                int i;
                                i = 0;
                                while ((i < jsonArray.length()) && (stop == 0)) {
                                    JSONObject pembeli = jsonArray.getJSONObject(i);
                                    name = pembeli.getString("nama");
                                    if (name.equals(un.toString())) {
                                        stop = 1;
                                        Toast.makeText(RegisterAct.this, "Username already exist", Toast.LENGTH_SHORT).show();
                                    }
                                    i++;
                                }

                                if (stop == 0) {
                                    SharedPreferences sp = getSharedPreferences(SP, MODE_PRIVATE);
                                    SharedPreferences.Editor ed = sp.edit();
                                    ed.putString("un", _nameText.getText().toString());
                                    ed.putString("pw", _passwordText.getText().toString());
                                    ed.putString("st","active");
                                    ed.commit();
                                    tambah();
                                    Toast.makeText(RegisterAct.this, "Register successful", Toast.LENGTH_SHORT).show();
                                    Intent in = new Intent(RegisterAct.this, MainActivity.class);
                                    startActivity(in);
                                    finish();
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


    void tambah() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM KK:mm a");
        lasttime = sdf.format(now);
        StringRequest postRequest = new StringRequest(Request.Method.POST, INSERT_PEMBELI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.i("FUCK", response);
                        Log.d("Response POST", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Menambahkan parameters post
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", "");
                params.put("nama", _nameText.getText().toString());
                params.put("pass", _passwordText.getText().toString());
                params.put("lat", "");
                params.put("long", "");
                params.put("req", "");
                params.put("lasttime", lasttime);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(postRequest);
    }
}

