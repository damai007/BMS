package com.lumachrome.bms.bms3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lumachrome.bms.bms3.maps.AppController;
import com.lumachrome.bms.bms3.maps.session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.lumachrome.bms.bms3.maps.maps2Activity;

public class LoginActivity extends AppCompatActivity {

    Button Loginbtn;
    EditText username,password;
    private ProgressDialog pDialog;
    session session;
    //private static String url_login="http://pancanaka.net/login.php";
    private static String url_login="http://megarkarsa.com/prajuritlogin.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_2);

        chekGPSandConnection();

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Sign-in In process please wait...");
        pDialog.setCancelable(false);

        session = new session(getApplicationContext());

        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        username.requestFocus();

        Loginbtn=(Button)findViewById(R.id.btnLogin);
        Loginbtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                showpDialog();
                //request data json tapi dijadiin string
                RequestQueue requesQueue = AppController.getInstance().getRequestQueue();
                StringRequest postRequest = new StringRequest(Request.Method.POST, url_login,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                try {
                                    JSONObject json=new JSONObject(response);
                                    JSONArray array = json.getJSONArray("user");

                                    for(int i=0;i<array.length();i++) {
                                        JSONObject c = array.getJSONObject(i);
                                        String id = c.getString("IdPrajurit");
                                        String Nama = c.getString("Nama");
                                        String Alamat = c.getString("Alamat");
                                        String TTL = c.getString("TTL");
                                        session.createLoginSession(id, Nama, Alamat, TTL);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //ini yang bikin error
                                if(session.isLoggedIn()){
                                    Intent nextScreen = new Intent(getApplicationContext(), maps2Activity.class);
                                    startActivity(nextScreen);
                                    //LoginActivity.this.startActivity(new Intent(LoginActivity.this, MenuUtama.class));
                                    //must finish application
                                    LoginActivity.this.finish();
                                    hidepDialog();
                                }


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                hidepDialog();

                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("username", username.getText().toString());
                        params.put("password", password.getText().toString());

                        return params;
                    }
                };
                requesQueue.add(postRequest);


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        //AlertDialog diaBox = AskOption();
        //diaBox.show();
        finish();
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to EXIT?")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    public void chekGPSandConnection(){
// Get Location Manager and check for GPS & Network location services
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }
}
