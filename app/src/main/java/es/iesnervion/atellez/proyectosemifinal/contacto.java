package es.iesnervion.atellez.proyectosemifinal;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//Clase del layout Activity_contacto
public class contacto extends AppCompatActivity implements View.OnClickListener {

    private Button btnTiwtter, btnGithub, btnFacebook, btnLinkedin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        btnGithub = (Button) findViewById(R.id.btnGithub);
        btnFacebook = (Button) findViewById(R.id.btnFacebook);
        btnLinkedin = (Button) findViewById(R.id.btnLinkedin);
        btnTiwtter = (Button) findViewById(R.id.btnTwitter);

        btnTiwtter.setOnClickListener(this);
        btnLinkedin.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);
        btnGithub.setOnClickListener(this);
    }
    //Metodos onClick de cada boton
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnGithub:
                String perfilGithub = "https://github.com/alvarotellez";
                Intent intentGithub = new Intent(Intent.ACTION_VIEW);
                intentGithub.setData(Uri.parse(perfilGithub));
                startActivity(intentGithub);
                break;
            case R.id.btnFacebook:
                String perfilFacebook = "https://www.facebook.com/alvaro.tellez.921";
                Intent intentFacebook = new Intent(Intent.ACTION_VIEW);
                intentFacebook.setData(Uri.parse(perfilFacebook));
                startActivity(intentFacebook);
                break;

            case R.id.btnTwitter:
                String perfilTwitter = "https://twitter.com/alvarotellez22";
                Intent intentTwitter = new Intent(Intent.ACTION_VIEW);
                intentTwitter.setData(Uri.parse(perfilTwitter));
                startActivity(intentTwitter);
                break;

            case R.id.btnLinkedin:
                String perfilLinkedin= "https://www.linkedin.com/in/alvarotellezhidalgo";
                Intent intentLinkedin = new Intent(Intent.ACTION_VIEW);
                intentLinkedin.setData(Uri.parse(perfilLinkedin));
                startActivity(intentLinkedin);
                break;

        }
    }
}
