package br.org.catolicasc.jogopokemon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button buttonOp1;
    private Button buttonOp2;
    private Button buttonOp3;
    private Button buttonOp4;
    private JSONArray jsonArrayGlobal;
    private String pokemonAtualGlobal;

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
                if(pokemonAtualGlobal.equals(nomePokemon)){
                    Toast.makeText(MainActivity.this,"Correto!", Toast.LENGTH_SHORT).show();

                    Random random = new Random();
                    int[] indice = new int[4];
                    for(int i =0; i<4;i++){
                        indice[i] = random.nextInt(151);
                    }

                    try {
                        buttonOp1.setText(jsonArrayGlobal.getJSONObject(indice[0]).getString("name"));
                        buttonOp2.setText(jsonArrayGlobal.getJSONObject(indice[1]).getString("name"));
                        buttonOp3.setText(jsonArrayGlobal.getJSONObject(indice[2]).getString("name"));
                        buttonOp4.setText(jsonArrayGlobal.getJSONObject(indice[3]).getString("name"));
                        ImageDownloader imageDownloader = new ImageDownloader();
                        int pokemon = indice[random.nextInt(4)];
                        pokemonAtualGlobal = jsonArrayGlobal.getJSONObject(pokemon).getString("name");
                        Bitmap imagem = imageDownloader.execute(jsonArrayGlobal.getJSONObject(pokemon).getString("img").replace("http","https")).get();
                        imageView.setImageBitmap(imagem);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

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
                jsonArrayGlobal = json.getJSONArray("pokemon");
                Random random = new Random();
                int[] indice = new int[4];
                for(int i =0; i<4;i++){
                    indice[i] = random.nextInt(151);
                }

                try {
                    buttonOp1.setText(jsonArrayGlobal.getJSONObject(indice[0]).getString("name"));
                    buttonOp2.setText(jsonArrayGlobal.getJSONObject(indice[1]).getString("name"));
                    buttonOp3.setText(jsonArrayGlobal.getJSONObject(indice[2]).getString("name"));
                    buttonOp4.setText(jsonArrayGlobal.getJSONObject(indice[3]).getString("name"));
                    ImageDownloader imageDownloader = new ImageDownloader();
                    int pokemon = indice[random.nextInt(4)];
                    pokemonAtualGlobal = jsonArrayGlobal.getJSONObject(pokemon).getString("name");
                    Bitmap imagem = imageDownloader.execute(jsonArrayGlobal.getJSONObject(pokemon).getString("img").replace("http","https")).get();
                    imageView.setImageBitmap(imagem);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

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
