package es.iesnervion.atellez.proyectosemifinal;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.query.internal.InFilter;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class Activity_emergencia extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    private static final String TAG="Activity_emergencia";
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;
    private long FASTEST_INTERVAL = 2000;
    private LocationManager locationManager;
    //Para la activity
    Button btnLlamar;
    String numIntroducido, nomUsuario;
    String longitud, latitud;
    String mensaje;
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


        //para la localizacion

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        checkLocation();
    }

    @Override
    public void onClick(View v) {

        checkLocation();
        String msg = "El usuario "+nomUsuario+" ha sufrido un accidente "+mensaje;

        //sendSMS(numIntroducido,msg);
        sendSMS(numIntroducido,msg+" "+mensaje);

    }

    public void sendSMS(String phoneNo, String msg) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para realizar llamadas telefónicas.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 225);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+34"+phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Mensaje enviado al numero " + phoneNo,
                    Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onConnected( Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            Log.i("Mensaje", "No se tiene permiso para obtener la localizacion.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 225);
        }
        
        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if ((mLocation != null)){
            startLocationUpdates();
        }
        if (mLocation != null){
            //AQUI SACAMOS EL VALOR DE LA LONGITUD Y LA LATITUD PARA EL MENSAJE
            longitud = String.valueOf(mLocation.getLongitude());
            latitud = String.valueOf(mLocation.getLatitude());
        }else{
            Toast.makeText(this,"Localización no encotrada",Toast.LENGTH_SHORT).show();
        }
    }
    //Este metodo realiza la actualizacion de la localizacion con el tiempo de refresco que le queramos poner
    private void startLocationUpdates() {

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para obtener la localizacion.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 225);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG,"Conexión Suspendida");
        mGoogleApiClient.connect();;
    }

    @Override
    public void onConnectionFailed( ConnectionResult connectionResult) {
        Log.i(TAG,"Error de conexion"+connectionResult.getErrorCode());
    }

    //AQUIIIIIIIIIII
    //Esta comentado pq no nos interesa que se muestre un toast cada vez que se actualice la ubicacion
    @Override
    public void onLocationChanged(Location location) {
        mensaje = "Se encuentra en la localización: "+ Double.toString(location.getLatitude())
                +" , "+ Double.toString(location.getLongitude());

        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }
    private boolean checkLocation() {
        if(!isLocationEnabled()){
            showAlert();
        }
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Activar Localizacion").setMessage("Su GPS esta desactivado, no podrá usar la aplicación").setPositiveButton("Configuracion de GPS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        });
    }

    private boolean isLocationEnabled(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }




}
