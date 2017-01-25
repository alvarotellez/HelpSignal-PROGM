package es.iesnervion.atellez.proyectosemifinal;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class configuracion extends AppCompatActivity {
    private ImageView logo_config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        //Hacer que rote la imagen en cuanto entre el usuario en esa pestaña
        logo_config = (ImageView) findViewById(R.id.img_config);
        Animation movimiento;
        movimiento = AnimationUtils.loadAnimation(this, R.anim.girar);
        movimiento.reset();
        logo_config.startAnimation(movimiento);
    }
}
