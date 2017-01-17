package es.iesnervion.atellez.proyectosemifinal;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.renderscript.Double2;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.query.internal.InFilter;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class Activity_emergencia extends AppCompatActivity implements View.OnClickListener, LocationListener {

    Button btnLlamar;
    private Switch localizacionActivada;
    String numIntroducido, nomUsuario;
    Double longitud, latitud;
    String mensaje;

    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencia);

        //Obtenemos el nombre del usuario
        Bundle bundle = getIntent().getExtras();
        nomUsuario = bundle.getString("nomUsuario");

        //Obtenemos el numero de telefono del usuario
        Bundle bundle1 = getIntent().getExtras();
        numIntroducido = bundle1.getString("numTelefono");

        //Localizamos el btn en el layout
        btnLlamar = (Button) findViewById(R.id.btnAyuda);
        btnLlamar.setOnClickListener(this);
        localizacionActivada = (Switch) findViewById(R.id.localizacion);

    }

    @Override
    public void onClick(View v) {
        //Si el switch esta activado envia un mensaje con la ubicacion
        //Si no esta activado realiza una llamada al numero de emergencias 112
        if (localizacionActivada.isChecked()) {

            String msg = "El usuario " + nomUsuario + " ha sufrido un accidente " + "Longitud:" +longitud +" Latitud: "+latitud;

            sendSMS(numIntroducido, msg + " " + mensaje);

            int permissionCheck = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CALL_PHONE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.i("Mensaje", "No se tiene permiso para realizar llamadas telefónicas.");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 225);
            } else {
                Log.i("Mensaje", "Se tiene permiso!");
                Intent callIntent = new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + numIntroducido)); //
                startActivity(callIntent);
                Toast.makeText(getApplicationContext(), "Realizando llamada al numero " + numIntroducido,
                        Toast.LENGTH_LONG).show();
            }
        } else {
            int permissionCheck = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CALL_PHONE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.i("Mensaje", "No se tiene permiso para realizar llamadas telefónicas.");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 225);
            } else {
                Log.i("Mensaje", "Se tiene permiso!");
                Intent callIntent = new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + 112)); //
                startActivity(callIntent);
                Toast.makeText(getApplicationContext(), "Realizando llamada al numero " + numIntroducido,
                        Toast.LENGTH_LONG).show();
            }
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria cri = new Criteria();
        String provider = locationManager.getBestProvider(cri, false);

        if (provider != null && !provider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("Mensaje", "No se puede obtener la ubicación");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 225);
            }
            Location location = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider,2000,1,this);


            if (location != null){
                onLocationChanged(location);
            }else{
                Toast.makeText(getApplicationContext(),"Localización no encontrada", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getApplicationContext(),"Provider is null", Toast.LENGTH_LONG).show();
        }

    }

    public void sendSMS(String phoneNo, String msg) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para realizar llamadas telefónicas.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 225);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+34" + phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Mensaje enviado al numero " + phoneNo,
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onLocationChanged(Location location) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //textView2.setText("Latitude"+latitud);
        latitud = location.getLatitude();
        //textView3.setText("Longitude"+location.getLongitude());
        longitud = location.getLongitude();
        if (location!=null){
            //textView2.setText("Latitude"+latitud);
            latitud = location.getLatitude();
            //textView3.setText("Longitude"+location.getLongitude());

            longitud = location.getLongitude();
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
}


