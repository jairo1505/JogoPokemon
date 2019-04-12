package br.org.catolicasc.jogopokemon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageView imageView;
    private Button buttonOp1;
    private Button buttonOp2;
    private Button buttonOp3;
    private Button buttonOp4;
    private String pokemonGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        buttonOp1 = findViewById(R.id.buttonOp1);
        buttonOp2 = findViewById(R.id.buttonOp2);
        buttonOp3 = findViewById(R.id.buttonOp3);
        buttonOp4 = findViewById(R.id.buttonOp4);

        final DownloadDeDados downloadDeDados = new DownloadDeDados();
        downloadDeDados.execute("https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json");

        View.OnClickListener listenerButtons = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                String nomePokemon = b.getText().toString();
                if(pokemonGlobal.equals(nomePokemon)){
                    Toast.makeText(MainActivity.this,"Correto!", Toast.LENGTH_SHORT).show();
                    final DownloadDeDados downloadDeDados = new DownloadDeDados();
                    downloadDeDados.execute("https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json");
                } else {
                    Toast.makeText(MainActivity.this,"Errado!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        buttonOp1.setOnClickListener(listenerButtons);
        buttonOp2.setOnClickListener(listenerButtons);
        buttonOp3.setOnClickListener(listenerButtons);
        buttonOp4.setOnClickListener(listenerButtons);
    }



    private class DownloadDeDados extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: começa com o parâmetro: " + strings[0]);
            String jsonFeed = downloadJson(strings[0]);
            if (jsonFeed == null) {
                Log.e(TAG, "doInBackground: Erro baixando JSON");
            }
            return jsonFeed;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: parâmetro é: " + s);
            JSONTokener jsonTokener = new JSONTokener(s);
            try {
                JSONObject json = new JSONObject(jsonTokener);
                JSONArray jsonArray = json.getJSONArray("pokemon");
                Random random = new Random();
                int[] indice = new int[4];
                for(int i =0; i<4;i++){
                    indice[i] = random.nextInt(7);
                }
                buttonOp1.setText(jsonArray.getJSONObject(indice[0]).getString("name"));
                buttonOp2.setText(jsonArray.getJSONObject(indice[1]).getString("name"));
                buttonOp3.setText(jsonArray.getJSONObject(indice[2]).getString("name"));
                buttonOp4.setText(jsonArray.getJSONObject(indice[3]).getString("name"));
                ImageDownloader imageDownloader = new ImageDownloader();
                int pokemon = indice[random.nextInt(4)];
                pokemonGlobal = jsonArray.getJSONObject(pokemon).getString("name");
                Bitmap imagem = imageDownloader.execute(jsonArray.getJSONObject(pokemon).getString("img").replace("http","https")).get();
                imageView.setImageBitmap(imagem);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        private String downloadJson(String urlString) {
            StringBuilder json = new StringBuilder();
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int resposta = connection.getResponseCode();
                Log.d(TAG, "downloadJson: O código de resposta foi: " + resposta);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                int charsLidos;
                char[] inputBuffer = new char[99999999];
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
                Log.e(TAG, "downloadJson: URL é inválida " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "downloadJson: Ocorreu um erro de IO ao baixar dados: "
                        + e.getMessage());
            }
            return null;
        }
    }
    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        private static final String TAG = "ImageDownloader";

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                Log.d(TAG, "doInBackground: Baixei imagem com sucesso!"+ url);

                return bitmap;
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: Erro ao baixar imagem " + e.getMessage());
            }
            return null;
        }
    }

}
