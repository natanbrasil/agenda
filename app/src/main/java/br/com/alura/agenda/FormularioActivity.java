package br.com.alura.agenda;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.File;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.modelo.Aluno;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class FormularioActivity extends AppCompatActivity {

    public static final int CODIGO_CAMERA = 123;
    private FormularioHelper helper;
    private String caminhoFoto;
    private ImageView campoFoto = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        helper = new FormularioHelper(this);


        Intent intent = getIntent();
        Aluno aluno = (Aluno) intent.getSerializableExtra("aluno");
        if (aluno != null) {
            helper.preencheFormulario(aluno);
        }

        Button botaoFoto = (Button) findViewById(R.id.formulario_botao_foto);
        botaoFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                caminhoFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
                File arquivoFoto = new File(caminhoFoto);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(FormularioActivity.this, BuildConfig.APPLICATION_ID + ".provider", arquivoFoto));
                startActivityForResult(intent, CODIGO_CAMERA);
            }
        });
    }



            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(resultCode == Activity.RESULT_OK){
            if (requestCode == CODIGO_CAMERA) {
                    Bitmap bitmap = BitmapFactory.decodeFile(caminhoFoto);
                    Bitmap bitmapReduzido = bitmap.createScaledBitmap(bitmap, 150, 150, true);
                    ImageView campoFoto = (ImageView) findViewById(R.id.formulario_foto);
                    campoFoto.setImageBitmap(bitmapReduzido);
                    campoFoto.setTag(caminhoFoto);
                }
            }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_formulario, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_formulario_ok:
                Aluno aluno = helper.PegaAluno();

                AlunoDAO dao = new AlunoDAO(this);
                if (aluno.getId() != null) {
                    dao.altera(aluno);
                }else{
                    dao.insere(aluno);
                }
                dao.close();

                Toast.makeText(FormularioActivity.this, "Aluno " + aluno.getNome() + " Salvo", Toast.LENGTH_SHORT).show();

        finish();
        break;
    }
        return super.onOptionsItemSelected(item);
    }
}
