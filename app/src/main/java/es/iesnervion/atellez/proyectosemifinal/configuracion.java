package es.iesnervion.atellez.proyectosemifinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class configuracion extends AppCompatActivity implements View.OnClickListener {
    private EditText etNomUsuario, etNumContacto;
    private Button btnGuardar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        etNomUsuario = (EditText) findViewById(R.id.nomUsuarioEditado);
        etNumContacto = (EditText) findViewById(R.id.numContactoEditado);

        //Obtenemos el nombre del usuario
        SharedPreferences prefNombreUsuario = getSharedPreferences("MisPreferencias",MODE_PRIVATE);
        String nomUsuario = prefNombreUsuario.getString("nomUsuario","");


        //Obtenemos el numero de telefono del usuario
        SharedPreferences prefNumContacto = getSharedPreferences("MisPreferencias",MODE_PRIVATE);
        String numIntroducido = prefNumContacto.getString("numContacto","");

        etNomUsuario.setText(nomUsuario);
        etNumContacto.setText(numIntroducido);

        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //Guardamos en las sharedpreferences los nuevos datos introducidos por el usuario
        SharedPreferences misPref = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = misPref.edit();
        editor.putString("nomUsuario",etNomUsuario.getText().toString());
        editor.putString("numContacto",etNumContacto.getText().toString());
        editor.commit();
        //Mostramos un mensaje informativo para el usuario
        Toast.makeText(getApplicationContext(), "Cambios guardados", Toast.LENGTH_LONG).show();
        //Lo llevamos de nuevo a la pantalla de socorro
        Intent intent = new Intent(v.getContext(), Activity_emergencia.class);
        startActivity(intent);
    }
}
