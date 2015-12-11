package br.edu.ifspsaocarlos.agenda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import br.edu.ifspsaocarlos.agenda.R;
import br.edu.ifspsaocarlos.agenda.data.ContatoDAO;
import br.edu.ifspsaocarlos.agenda.model.Contato;

public class DetalheActivity extends AppCompatActivity {
    private Contato c;
    private ContatoDAO cDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("contact"))
        {

            this.c = (Contato) getIntent().getSerializableExtra("contact");
            EditText nameText = (EditText)findViewById(R.id.editText1);
            nameText.setText(c.getNome());

            EditText foneText = (EditText)findViewById(R.id.editText2);
            foneText.setText(c.getFone());

            EditText emailText = (EditText)findViewById(R.id.editText3);
            emailText.setText(c.getEmail());

            int pos =c.getNome().indexOf(" ");
            if (pos==-1)
                pos=c.getNome().length();

            setTitle(c.getNome().substring(0,pos));
        }


        cDAO = new ContatoDAO(this);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detalhe, menu);
        if (!getIntent().hasExtra("contact"))
        {
            MenuItem item = menu.findItem(R.id.delContato);
            item.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.salvarContato:
                salvar();
                return true;
            case R.id.delContato:
                cDAO.deleteContact(c);
                Toast.makeText(getApplicationContext(), "Removido com sucesso", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                setResult(RESULT_OK,resultIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void salvar()
    {
        String name = ((EditText) findViewById(R.id.editText1)).getText().toString();
        String fone = ((EditText) findViewById(R.id.editText2)).getText().toString();
        String email = ((EditText) findViewById(R.id.editText3)).getText().toString();

        if (c==null)
        {
            c = new Contato();
            c.setNome(name);
            c.setFone(fone);
            c.setEmail(email);

            cDAO.createContact(c);
            Toast.makeText(this, "Incluído com sucesso", Toast.LENGTH_SHORT).show();
        }
        else
        {
            c.setNome(name);
            c.setFone(fone);
            c.setEmail(email);

            cDAO.updateContact(c);
            Toast.makeText(this, "Alterado com sucesso", Toast.LENGTH_SHORT).show();
        }

        Intent resultIntent = new Intent();
        setResult(RESULT_OK,resultIntent);
        finish();
    }


}
