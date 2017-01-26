package es.iesnervion.atellez.proyectosemifinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class activity_carga extends AppCompatActivity {
    private ImageView img_carga;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_carga);

        //Hacemos girar la imagen de carga
        img_carga = (ImageView) findViewById(R.id.imgCarga);
        Animation movimiento;
        movimiento = AnimationUtils.loadAnimation(this, R.anim.girar);
        movimiento.reset();
        img_carga.startAnimation(movimiento);

        //Deja la pantalla parada durante 1 segundo para comprobar si existe el sharedPreferences con el nombre de usuario
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                    SharedPreferences shf = getSharedPreferences("MisPreferencias",MODE_PRIVATE);
                    String nomUsuario = shf.getString("nomUsuario",null);
                    String numContacto = shf.getString("numContacto",null);

                    //Comprueba que la sharedPreferences existe y dependiendo del estado nos manda a una pantalla u otra:
                    //1. Si no hay shared preferences nos manda a rellenar el login
                    //2. Si hay shared preferences nos manda al activity_emergencia
                    if (nomUsuario !=null && numContacto!=null){
                        Intent intent2 = new Intent(activity_carga.this,Activity_emergencia.class);
                        startActivity(intent2);
                    }else{
                        Intent intent = new Intent(activity_carga.this,MainActivity.class);
                        startActivity(intent);
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}
