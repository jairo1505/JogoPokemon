package br.org.catolicasc.jogopokemon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageView imageView;
    private Button buttonOp1;
    private Button buttonOp2;
    private Button buttonOp3;
    private Button buttonOp4;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        buttonOp1 = findViewById(R.id.buttonOp1);
        buttonOp2 = findViewById(R.id.buttonOp2);
        buttonOp3 = findViewById(R.id.buttonOp3);
        buttonOp4 = findViewById(R.id.buttonOp4);

        String jsonPokemon = lerArquivo("https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json");
        List<JsonPokemon> pokemons = leFaturasDeJSONString(jsonPokemon);
        String nomes = "";
        for (JsonPokemon pokemon : pokemons) {
            nomes += pokemon.getName();
        }
        textView.setText(nomes);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String lerArquivo(String arquivo) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linhaAtual;
            while ((linhaAtual = br.readLine()) != null)
            {
                builder.append(linhaAtual).append("\n");
            }
        }
        catch (IOException e) {
            System.err.println("Erro lendo arquivo: " + e.getMessage());
        }

        return builder.toString();
    }

    private static List<JsonPokemon> leFaturasDeJSONString(String jsonString) {
        List<JsonPokemon> listaPokemons = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray pokemons = json.getJSONArray("pokemon");

            for (int i = 0; i < pokemons.length(); i++) {
                JSONObject pokemon = pokemons.getJSONObject(i);
                JsonPokemon f = new JsonPokemon(
                        pokemon.getString("name"),
                        pokemon.getString("img")
                );
                listaPokemons.add(f);
            }
        } catch (JSONException e) {
            System.err.println("Erro fazendo parse de String JSON: " + e.getMessage());
        }

        return listaPokemons;
    }

}
