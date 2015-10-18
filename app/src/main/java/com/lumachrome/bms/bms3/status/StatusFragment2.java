package com.lumachrome.bms.bms3.status;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.StringRequest;
import com.lumachrome.bms.bms3.R;
import com.lumachrome.bms.bms3.maps.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by damai007 on 8/16/2015.
 */
public class StatusFragment2 extends AppCompatActivity {

    private String url_info = "http://bms.comze.com/json/statusprajurit.php";

    private ProgressDialog pDialog;

    private SeekBar seekBar_HP, seekBar_Ammo;
    private int progressStatus_HP = 80;
    private int progressStatus_Ammo = 100;

    private TableLayout stk;

    private TextView hp_value, ammo_value;
    private TextView id, name, gender, born, rank, rank_title, division, headquarters, academy, address;

    private String value_id, value_name, value_gender, value_address, value_born,
            value_rank, value_academy, value_division, value_headquarters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_status2);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Retrieving soldier info ...");
        pDialog.setCancelable(false);

        showpDialog();

        // Untuk seekBar Health Points dan Ammunition //
        setHpAndAmmo();

        // Untuk Info Status Prajurit //
        setInfoStatus();

        SeekBar.OnSeekBarChangeListener seekBarHPListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setSecondaryProgress(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //add code here
            }

            @Override
            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                hp_value.setText(progress + " / 100");
            }
        };
        seekBar_HP.setOnSeekBarChangeListener(seekBarHPListener);

        SeekBar.OnSeekBarChangeListener seekBarAmmoListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setSecondaryProgress(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //add code here
            }

            @Override
            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                ammo_value.setText("" + progress);
            }
        };
        seekBar_Ammo.setOnSeekBarChangeListener(seekBarAmmoListener);

        /* Inisialisasi Table Layout untuk daftar keahlian prajurit */
        stk = (TableLayout) findViewById(R.id.table_list);

        // Ambil data info prajurit dari Internet //
        getInfoSoldier();
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void setHpAndAmmo() {
        hp_value = (TextView) findViewById(R.id.hp_value2);
        ammo_value = (TextView) findViewById(R.id.ammo_value2);
        hp_value.setText(progressStatus_HP + " / 100");
        ammo_value.setText("" + progressStatus_Ammo);

        seekBar_HP = (SeekBar) findViewById(R.id.hp_seekbar);
        seekBar_Ammo = (SeekBar) findViewById(R.id.ammo_seekbar);
        seekBar_HP.setProgress(progressStatus_HP);
        seekBar_Ammo.setProgress(progressStatus_Ammo);
    }

    private void setInfoStatus() {
        id = (TextView) findViewById(R.id.id_soldier);
        name = (TextView) findViewById(R.id.name_soldier);
        gender = (TextView) findViewById(R.id.gender);
        born = (TextView) findViewById(R.id.born_date);
        rank = (TextView) findViewById(R.id.rank);
        rank_title = (TextView) findViewById(R.id.rank_judul);
        division = (TextView) findViewById(R.id.division);
        headquarters = (TextView) findViewById(R.id.headquarters);
        academy = (TextView) findViewById(R.id.academy);
        address = (TextView) findViewById(R.id.address);
    }

    private void changeTextRun() {
        id.setText(value_id);
        name.setText(value_name);
        gender.setText(value_gender);
        born.setText(value_born);
        rank.setText(value_rank);
        rank_title.setText(value_rank);
        division.setText(value_division);
        headquarters.setText(value_headquarters);
        academy.setText(value_academy);
        address.setText(value_address);
    }

    private void insertRowTable(String value) {
        TableRow row = new TableRow(this);
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextColor(Color.BLACK);
        text.setGravity(Gravity.CENTER);
        row.addView(text);
        stk.addView(row);
    }

    private void getInfoSoldier() {
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url_info,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray array = json.getJSONArray("prajurit");
                            String keahlian;

                            for(int i=0; i<array.length(); i++) {
                                // inisialisasi arraylist string untuk list_keahlian
                                JSONObject c = array.getJSONObject(i);
                                value_id = c.getString("id");
                                value_name = c.getString("nama");
                                value_gender = c.getString("gender");
                                value_address = c.getString("alamat");
                                value_born = c.getString("ttl");
                                value_rank = c.getString("pangkat");
                                value_academy = c.getString("pendidikan");
                                value_division = c.getString("divisi");
                                value_headquarters = c.getString("markas");

                                JSONObject skill = c.getJSONObject("keahlian");
                                int jum_keahlian = Integer.parseInt(skill.getString("jumlah"));
                                //Log.d("ZZZ", "jumlah = "+jum_keahlian);

                                if (jum_keahlian == 0) {
                                    insertRowTable("-");
                                } else {
                                    for (int j = 0; j < jum_keahlian; j++) {
                                        keahlian = skill.getString("list_" + (j + 1));
                                        insertRowTable(keahlian);
                                        //Log.d("ZZZ", "skill : " + keahlian);
                                    }
                                }
                            }
                            changeTextRun();
                            hidepDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", "PR001");

                return params;
            }
        };
        requestQueue.add(postRequest);
    }
}
