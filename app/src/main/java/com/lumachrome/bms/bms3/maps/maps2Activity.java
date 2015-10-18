package com.lumachrome.bms.bms3.maps;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.lumachrome.bms.bms3.R;
import com.lumachrome.bms.bms3.chat.ListPeople;
import com.lumachrome.bms.bms3.status.StatusFragment2;

import java.util.HashMap;

public class maps2Activity extends AppCompatActivity implements LocationListener {

    private session session;
    // contacts JSONArray
    private String url = "http://megarkarsa.com/gpsjson.php";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    updateAndRequestajsonPosition update;
    LocationManager lm;
    String provider;
    Location myLocation;

    //boolean gps_enabled = false;
    //boolean network_enabled = false;

    Criteria criteria;
    //session nya
    static String idprajurit;
    static String alamat;
    static String nama;
    static String ttl;

    private TextView mCounter;
    private int count = 1;

    TextView latitudetext, longitudetext;
    LinearLayout statusbar,Chatbar,bottombox;
    Button sendbtn;
    EditText msgBox;

    private boolean HideChatboxAndMessage=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        setUpMapIfNeeded();

        ActionBar actionBar = getActionBar();

        msgBox=(EditText)findViewById(R.id.messagebox);
        sendbtn=(Button)findViewById(R.id.sendbtn);

        bottombox=( LinearLayout)findViewById(R.id.bottombox);
        statusbar=( LinearLayout)findViewById(R.id.StatusbarLatLing);
        Chatbar=( LinearLayout)findViewById(R.id.chatbox);
        statusbar.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
            if(HideChatboxAndMessage) {
                msgBox.setVisibility(View.VISIBLE);
                sendbtn.setVisibility(View.VISIBLE);
                sendbtn.setWidth(10);
                sendbtn.setHeight(10);
                Context context = getApplicationContext();
                Toast.makeText(context, "linear layout ke klik", Toast.LENGTH_SHORT).show();
                HideChatboxAndMessage=false;
            }else{
                msgBox.setVisibility(View.GONE);
                sendbtn.setVisibility(View.GONE);
                Context context = getApplicationContext();
                Toast.makeText(context, "linear layout ke klik", Toast.LENGTH_SHORT).show();
                HideChatboxAndMessage=true;
            }
            }
        });

        latitudetext = (TextView) findViewById(R.id.Latitude);
        longitudetext = (TextView) findViewById(R.id.Longitude);
        update = new updateAndRequestajsonPosition();
        session = new session(getApplicationContext());

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        idprajurit = user.get(session.KEY_ID);
        nama = user.get(session.KEY_NAMA);
        alamat = user.get(session.KEY_ALAMAT);
        ttl = user.get(session.KEY_TTL);

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        //mesti ambil contex..gatau knp error klo ga pake contex, padahal awalnya jalan..ga pake contex juga
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, this);
        }
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 5, this);
        }
        provider = lm.getBestProvider(criteria, true);
        myLocation = lm.getLastKnownLocation(provider);

        if (myLocation != null) {
            double longitude = myLocation.getLongitude();
            double latitude = myLocation.getLatitude();
            latitudetext.setText("Latitude : \n" + String.valueOf(latitude));
            longitudetext.setText("Longitude : \n" + String.valueOf(longitude));

            LatLng latLng = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(idprajurit + " " + nama).snippet("latitude : " + latitude + " longitude" + longitude));

            for(int i =0;i<update.getSize();i++){
                mMap.addMarker(new MarkerOptions().position(new LatLng(update.getLatitude(i), update.getLongitude(i))).title(update.getIDPrajurit(i)).snippet("latitude : " + update.getLatitude(i) + "- longitude :" + update.getLongitude(i)));
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Where am I?"));
            ErrorConnection();
        }

        addAreaMission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        //    mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_maps2, menu);
        //return true;
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.menu_chat).getActionView();
        mCounter = (TextView) badgeLayout.findViewById(R.id.counter);

        updateHotCount(count);

        final View menu_chat = menu.findItem(R.id.menu_chat).getActionView();

        new MyMenuItemStuffListener(menu_chat, "Chat Messages") {
            @Override
            public void onClick(View v) {
                Intent nextScreen = new Intent(getApplicationContext(), ListPeople.class);
                startActivity(nextScreen);
            }
        };
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_chat:
                //Intent chatScreen = new Intent(getApplicationContext(), ListPeople.class);
                //startActivity(chatScreen);
                return true;
            case R.id.menu_status:
                Intent statusScreen = new Intent(getApplicationContext(), StatusFragment2.class);
                startActivity(statusScreen);
                return true;
            case R.id.menu_about:
                // refresh
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        addAreaMission();

        myLocation = lm.getLastKnownLocation(provider);
        if (myLocation != null) {
            double longitude = myLocation.getLongitude();
            double latitude = myLocation.getLatitude();
            latitudetext.setText("Latitude : \n" + String.valueOf(latitude));
            longitudetext.setText("Longitude : \n" + String.valueOf(longitude));

            LatLng latLng = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(idprajurit + " " + nama).snippet("latitude : " + latitude + " longitude" + longitude));
            update.updateAndRequest(url, idprajurit, latitude, longitude);
            for(int i =0;i<update.getSize();i++){
                mMap.addMarker(new MarkerOptions().position(new LatLng(update.getLatitude(i), update.getLongitude(i))).title(update.getIDPrajurit(i)).snippet("latitude : " + update.getLatitude(i) + "- longitude :" + update.getLongitude(i)));
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Where am I?"));
            ErrorConnection();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onBackPressed() {
        AlertDialog diaBox = AskOption();
        diaBox.show();
    }

    private void addAreaMission(){
        PolygonOptions rectOptions = new PolygonOptions();

        rectOptions.add(new LatLng(-6.2584689, 107.1462146));
        rectOptions.add(new LatLng(-6.24887, 107.159175));
        rectOptions.add(new LatLng(-6.254455, 107.156951));
        // menambahkan warna #AARRGGBB pada polygon dan garis tepi
        rectOptions.fillColor(Color.parseColor("#8005B7E8")).strokeWidth(5).strokeColor(Color.CYAN);

        mMap.addPolygon(rectOptions);

    }

    private AlertDialog AskOption() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                        session.logoutUser();
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

    private void ErrorConnection() {
        AlertDialog alertDialog = new AlertDialog.Builder(maps2Activity.this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Cannot get yout location");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    // call the updating code on the main thread,
    // so we can call this asynchronously
    public void updateHotCount(final int new_count) {
        count = new_count;
        if (mCounter == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (new_count == 0)
                    mCounter.setVisibility(View.INVISIBLE);
                else {
                    mCounter.setVisibility(View.VISIBLE);
                    mCounter.setText(Integer.toString(new_count));
                }
            }
        });
    }

    /** Abstract class untuk Long Click and Clickable pada menu Chat di actionBar **/
    static abstract class MyMenuItemStuffListener implements View.OnClickListener, View.OnLongClickListener {
        private String hint;
        private View view;

        MyMenuItemStuffListener(View view, String hint) {
            this.view = view;
            this.hint = hint;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override abstract public void onClick(View v);

        @Override public boolean onLongClick(View v) {
            final int[] screenPos = new int[2];
            final Rect displayFrame = new Rect();
            view.getLocationOnScreen(screenPos);
            view.getWindowVisibleDisplayFrame(displayFrame);
            final Context context = view.getContext();
            final int width = view.getWidth();
            final int height = view.getHeight();
            final int midy = screenPos[1] + height / 2;
            final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            Toast cheatSheet = Toast.makeText(context, hint, Toast.LENGTH_SHORT);
            if (midy < displayFrame.height()) {
                cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT,
                        screenWidth - screenPos[0] - width / 2, height);
            } else {
                cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
            }
            cheatSheet.show();
            return true;
        }
    }
}
