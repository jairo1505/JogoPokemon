package br.org.catolicasc.jogopokemon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ListPokemon extends AppCompatActivity {
    private ListView listPokemon;
    private ArrayList<ListEntry> listaPokemons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);



        Intent intent = getIntent();

        String listAsString = intent.getStringExtra("lista");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ListEntry>>(){}.getType();
        listaPokemons = gson.fromJson(listAsString, type);

        listPokemon = findViewById(R.id.listPokemon);

        ListAdapter listAdapter = new ListAdapter(ListPokemon.this,R.layout.list_complex_item, listaPokemons);
        listPokemon.setAdapter(listAdapter);
    }
}
