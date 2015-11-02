package br.edu.ifspsaocarlos.agenda.adapter;



import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;

import android.widget.TextView;

import br.edu.ifspsaocarlos.agenda.R;
import br.edu.ifspsaocarlos.agenda.model.Contato;

public class ContatoArrayAdapter extends ArrayAdapter <Contato>  {
    private LayoutInflater inflater;



    public ContatoArrayAdapter(Activity activity, List<Contato> objects) {
        super(activity, R.layout.contato_celula, objects);



        this.inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    /*Quando a ListView usa o adapter para popular suas views, o método getView()
     * é chamado para cada célula. O parâmetro convertView é uma forma do adapter
     * reusar/reciclar views antigas. Desta forma, a ListView pode enviar ao adapter
     * views que não estão sendo mais usadas, em vez de instanciar novas.
      */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contato_celula, null);
            holder = new ViewHolder();
            holder.nome = (TextView) convertView.findViewById(R.id.nome);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contato c = getItem(position);
        holder.nome.setText(c.getNome());
        return convertView;
    }

    static class ViewHolder {
        public TextView nome;
    }



}


