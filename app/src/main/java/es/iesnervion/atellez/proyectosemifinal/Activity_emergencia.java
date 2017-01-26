package es.iesnervion.atellez.proyectosemifinal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;


public class Activity_emergencia extends AppCompatActivity implements View.OnClickListener, LocationListener {
    Button btnLlamar;
    private Switch localizacionActivada;
    //Cadena de caracteres que el usuario nos ha introducido en la primera pantalla
    String numIntroducido, nomUsuario;
    //Double con la latitud y la longitud
    Double latitud, longitud;
    //String con la longitud y la latitud de la posicion actual convertida del double
    String lat, longi;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencia);

        //Obtenemos el nombre del usuario
        SharedPreferences prefNombreUsuario = getSharedPreferences("MisPreferencias",MODE_PRIVATE);
        nomUsuario = prefNombreUsuario.getString("nomUsuario","");


        //Obtenemos el numero de telefono del usuario
        SharedPreferences prefNumContacto = getSharedPreferences("MisPreferencias",MODE_PRIVATE);
        numIntroducido = prefNumContacto.getString("numContacto","");

        //Localizamos el btn en el layout
        btnLlamar = (Button) findViewById(R.id.btnAyuda);
        btnLlamar.setOnClickListener(this);
        localizacionActivada = (Switch) findViewById(R.id.localizacion);


        //PARA LA LOCALIZACION ---> IMPLEMENTACION DEBAJO
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria cri = new Criteria();
        String provider = locationManager.getBestProvider(cri, false);

        if (provider != null && !provider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("Mensaje", "No se puede obtener la ubicación");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 225);
            }
            Location location = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider, 2000, 1, this);


            if (location != null) {
                onLocationChanged(location);
            } else {
                Toast.makeText(getApplicationContext(), "Localización no encontrada", Toast.LENGTH_LONG).show();
            }

        } else {
            Log.i("Mensaje", "Provider is null");
        }
    }

    //Para mostrar el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //Para saber que ha seleccionado el usuario
    //Falta hacer que mande al usuario a la pestaña que seleccione
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent miConfiguracion = new Intent(Activity_emergencia.this, configuracion.class);
                Activity_emergencia.this.startActivity(miConfiguracion);
                break;
            case R.id.action_contact:
                Intent miContacto = new Intent(Activity_emergencia.this, contacto.class);
                Activity_emergencia.this.startActivity(miContacto);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        //Si el switch esta activado envia un mensaje con la ubicacion
        //Si no esta activado realiza una llamada al numero de emergencias 112
        if (localizacionActivada.isChecked()) {

            //Cadena que enviamos
            String msg = "El usuario " + nomUsuario + " ha sufrido un accidente. Localización:"+lat+", "+longi;
            //Llamada al metodo para envaiar el sms
            sendSMS(numIntroducido, msg);

            //Comprobamos que se tienen los permisos para enviar sms y llamar
            int permissionCheck = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CALL_PHONE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.i("Mensaje", "No se tiene permiso para realizar llamadas telefónicas.");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 225);
            } else {
                Log.i("Mensaje", "Se tiene permiso!");
                Intent callIntent = new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + numIntroducido)); //Llama al numero que ha introducido el usuario
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
                        Uri.parse("tel:" + 112)); //Llama al numero de emergencias
                startActivity(callIntent);
                Toast.makeText(getApplicationContext(), "Realizando llamada al numero " + 112,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    //Metodo para enviar el sms
    public void sendSMS(String phoneNo, String msg) {
        //Comprueba el permiso
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para realizar llamadas telefónicas.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 225);
        } else {
            //Envia sms
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+34" + phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Mensaje enviado al numero " + phoneNo,
                    Toast.LENGTH_LONG).show();
        }
    }


    //PARA LA LOCALIZACION
    //Metodos que hay que definir al implementar LocationListener
    @Override
    public void onLocationChanged(Location location) {

        //Comprueba si tiene permiso y pide permiso si no lo tiene y si tiene permiso obtiene la longitud actual
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para obtener la localizacion");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 225);
        }else{
            if (location != null) {
                //Sacamos la latitud y la longitud
                latitud = location.getLatitude();
                longitud = location.getLongitude();

                //Convertimos la latitud y longitud
                longi = String.valueOf(longitud);
                lat = String.valueOf(latitud);
            }
        }
    }
    //Falta esto
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


