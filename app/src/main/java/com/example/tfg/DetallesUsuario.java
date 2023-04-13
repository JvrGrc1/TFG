package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class DetallesUsuario extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private LinearLayout linearLayoutCodigo;
    private EditText codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_usuario);

        radioGroup = findViewById(R.id.radio_group);
        linearLayoutCodigo = findViewById(R.id.linearLayoutCodigo);
        codigo = findViewById(R.id.codigoRol);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = radioGroup.findViewById(checkedId);
                switch (radioButton.getId()){
                    case 1:
                        linearLayoutCodigo.setVisibility(View.VISIBLE);
                        codigo.setHint("Administrador");
                        break;
                    case 2:
                        linearLayoutCodigo.setVisibility(View.VISIBLE);
                        codigo.setHint("Entrenador");
                        break;
                    case 3:
                        linearLayoutCodigo.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        Toast.makeText(DetallesUsuario.this, radioButton.getId() + "", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


    }
}