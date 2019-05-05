package br.org.catolicasc.jogopokemon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter {
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private ArrayList<ListEntry> aplicativos;

    public ListAdapter(Context context, int resource, ArrayList<ListEntry> aplicativos) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.aplicativos = aplicativos;
    }

    @Override
    public int getCount() {
        return aplicativos.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ListEntry appAtual = aplicativos.get(position);

        viewHolder.nomePokemon.setText(appAtual.getNome());
        if(appAtual.getNomeRespondido()==null){
            viewHolder.nomeRespondido.setText("Você não respondeu!");
        } else {
            viewHolder.nomeRespondido.setText("Você respondeu: " + appAtual.getNomeRespondido());
        }

        //Picasso.with(getContext()).load(appAtual.getImgUrl()).placeholder(R.drawable.ic_launcher_background).into(viewHolder.imgPokemon);

        Picasso.Builder builder = new Picasso.Builder(getContext());
        builder.downloader(new OkHttpDownloader(getContext()));
        builder.build().load(appAtual.getImgUrl()).placeholder(R.drawable.ic_launcher_background).into(viewHolder.imgPokemon);

        if(appAtual.getNome().equals(appAtual.getNomeRespondido())) {
            convertView.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_green_light));
        } else {
            convertView.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_red_light));
        }


        return convertView;
    }

    private class ViewHolder {
        final TextView nomePokemon;
        final TextView nomeRespondido;
        final ImageView imgPokemon;

        ViewHolder(View v) {
            this.nomePokemon = v.findViewById(R.id.nomePokemon);
            this.nomeRespondido = v.findViewById(R.id.nomeRespondido);
            this.imgPokemon = v.findViewById(R.id.imgPokemon);
        }
    }
}
