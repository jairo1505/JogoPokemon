package br.org.catolicasc.jogopokemon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private TextView placar;
    private ImageView imageView;
    private Button buttonOp1;
    private Button buttonOp2;
    private Button buttonOp3;
    private Button buttonOp4;
    private String pokemonAtual;
    private int pokemonIndice = 0;
    private ArrayList<JsonPokemon> listaPokemon = new ArrayList<>();
    private int acertos = 0;
    private int erros = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        placar = findViewById(R.id.placar);
        imageView = findViewById(R.id.imageView);
        buttonOp1 = findViewById(R.id.buttonOp1);
        buttonOp2 = findViewById(R.id.buttonOp2);
        buttonOp3 = findViewById(R.id.buttonOp3);
        buttonOp4 = findViewById(R.id.buttonOp4);

        final DownloadDeDados downloadDeDados = new DownloadDeDados();
        downloadDeDados.execute("https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json");

        View.OnClickListener listenerButtons = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                String nomePokemon = b.getText().toString();
                if(pokemonIndice<listaPokemon.size()) {
                    if (pokemonAtual.equals(nomePokemon)) {
                        acertos++;
                        pokemonShow();

                    } else {
                        erros++;
                        Toast.makeText(MainActivity.this, "A resposta correta seria: " + pokemonAtual, Toast.LENGTH_SHORT).show();
                        pokemonShow();
                    }
                } else {
                    placar.setText("Acertos: " + acertos + " | Erros: " + erros+" | Fim de Jogo!");
                }
            }
        };
        buttonOp1.setOnClickListener(listenerButtons);
        buttonOp2.setOnClickListener(listenerButtons);
        buttonOp3.setOnClickListener(listenerButtons);
        buttonOp4.setOnClickListener(listenerButtons);
    }

    protected void pokemonShow() {
        placar.setText("Acertos: " + acertos + " | Erros: " + erros);
        ArrayList<Integer> numerosAleatorios = new ArrayList<>();
        for(int i=0; i<151; i++){
            if(i!=pokemonIndice){
                numerosAleatorios.add(i);
            }
        }
        Collections.shuffle(numerosAleatorios);
        ArrayList<Integer> indice = new ArrayList<>();
        indice.add(pokemonIndice);
        for(int i=0; i<3; i++){
            indice.add(numerosAleatorios.get(i));
        }

        Collections.shuffle(indice);

        try {
            buttonOp1.setText(listaPokemon.get(indice.get(0)).getName());
            buttonOp2.setText(listaPokemon.get(indice.get(1)).getName());
            buttonOp3.setText(listaPokemon.get(indice.get(2)).getName());
            buttonOp4.setText(listaPokemon.get(indice.get(3)).getName());
            ImageDownloader imageDownloader = new ImageDownloader();
            pokemonAtual = listaPokemon.get(pokemonIndice).getName();
            Bitmap imagem = imageDownloader.execute(listaPokemon.get(pokemonIndice).getImg().replace("http", "https")).get();
            imageView.setImageBitmap(imagem);
            pokemonIndice++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private class DownloadDeDados extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String jsonFeed = downloadJson(strings[0]);
            if (jsonFeed == null) {
                Toast.makeText(MainActivity.this, "Erro ao baixar arquivo", Toast.LENGTH_SHORT).show();
            }
            return jsonFeed;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONTokener jsonTokener = new JSONTokener(s);
            try {
                JSONObject json = new JSONObject(jsonTokener);
                JSONArray jsonArray = json.getJSONArray("pokemon");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JsonPokemon jsonPokemon = new JsonPokemon(
                            jsonArray.getJSONObject(i).getString("name"),
                            jsonArray.getJSONObject(i).getString("img"));
                    listaPokemon.add(jsonPokemon);
                }
                Collections.shuffle(listaPokemon);
                pokemonShow();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String downloadJson(String urlString) {
            StringBuilder json = new StringBuilder();
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int resposta = connection.getResponseCode();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                int charsLidos;
                char[] inputBuffer = new char[5000];
                while (true) {
                    charsLidos = reader.read(inputBuffer);
                    if (charsLidos < 0) {
                        break;
                    }
                    if (charsLidos > 0) {
                        json.append(
                                String.copyValueOf(inputBuffer, 0, charsLidos));
                    }
                }
                reader.close();
                return json.toString();

            } catch (MalformedURLException e) {
                Toast.makeText(MainActivity.this, "URL é inválida", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Ocorreu um erro de IO ao baixar dados", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();

                return BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Erro ao baixar imagem", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

}
