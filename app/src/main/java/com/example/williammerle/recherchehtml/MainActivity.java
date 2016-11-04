package com.example.williammerle.recherchehtml;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // On cible le boutton Rechercher
        View v1 = findViewById(R.id.buttonRecherche);
        Button b1 = (Button) v1;

        // On cible le boutton web
        View v5 = findViewById(R.id.buttonWeb);
        Button b5 = (Button) v5;

        // On rend scrollable le textview du HTML
        View v3 = findViewById(R.id.resultatHtml);
        TextView t3 = (TextView) v3;
        t3.setMovementMethod(new ScrollingMovementMethod());

        // On active l'évènement click sur le boutton submit en appelant la function doClickOnButton()
        b1.setOnClickListener(new View.OnClickListener() { // whatever happens, we make sure we call the next function on the MAIN thread
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            doClickOnButton();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        // On active l'évènement click sur le boutton Webview en appelant la function doWebViewButton()

        b5.setOnClickListener(new View.OnClickListener() { // whatever happens, we make sure we call the next function on the MAIN thread
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            doWebViewButton();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void doWebViewButton() throws IOException {

        // On récupère la value de l'input (l'url)
        View v2 = findViewById(R.id.searchUrl);
        EditText et2 = (EditText) v2;
        String st = et2.getText().toString();

        // On ouvre et affichons l'URL choisie
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(st));
        startActivity(i);
    }
    private void doClickOnButton() throws IOException {

        // On récupère la value de l'input (l'url)
        View v2 = findViewById(R.id.searchUrl);
        EditText et2 = (EditText) v2;
        final String st = et2.getText().toString();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                // On le hash pour avoir un nom de fichier unique et sans URL
                int hashCode = st.hashCode();

                // On définit le fichier cache
                File internal = getFilesDir();
                File f = new File(internal, "" + hashCode);

                // On définit la différence de temps entre la recherche et la date de création du fichier
                long lm = f.lastModified();
                Date d = new Date();
                long gt = d.getTime();
                long dif = gt - lm;
                int sevday = 604800000; // Configuration pour 7 jours (nombre de millisecondes en 7jours = 7*24*60*60*1000)

                // On appele la classe new URL en fonction de l'URL rentrée
                URL u = null;
                try {
                    u = new URL(st);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                // On ouvre la connexion

                URLConnection c = null;
                try {
                    c = u.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream cis = null;

                // On InputStream pour verifier si il y a des données (donc que l'URL existe)

                try {
                    cis = c.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Si u est null (on a rentré quelquechose)
                if (!cis.equals(null)) {

                    // On veréfie si le fichier existe déjà, sinon on le crée
                    if (f.exists() && f.isFile() && dif < sevday) {

                        //On lit le fichier
                        String rf = null;

                        try {
                            rf = this.readFile(f);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //On remplace le textview par ce qu'il y a dans le fichier
                        final String finalRf = rf;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                View v3 = findViewById(R.id.resultatHtml);
                                TextView t3 = (TextView) v3;
                                t3.setText(finalRf);
                            }
                        });


                    } else if(f.exists() && f.isFile() && dif > sevday){

                        f.delete();

                        // On crée le fichier car il existe pas
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // On va chercher le HTML de l'URL
                        String html = null;
                        try {
                            html = this.readUrl(u);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // On va ecrire le HTML dans son fichier
                        try {
                            this.writeFile(f, html);
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                        //On remplace le textview par ce qu'il y a dans le fichier
                        final String finalHtml = html;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                View v3 = findViewById(R.id.resultatHtml);
                                TextView t3 = (TextView) v3;
                                t3.setText(finalHtml);
                            }
                        });

                    }else {

                        // On crée le fichier car il existe pas
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // On va chercher le HTML de l'URL
                        String html = null;
                        try {
                            html = this.readUrl(u);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // On va ecrire le HTML dans son fichier
                        try {
                            this.writeFile(f, html);
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                        //On remplace le textview par ce qu'il y a dans le fichier
                        final String finalHtml = html;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                View v3 = findViewById(R.id.resultatHtml);
                                TextView t3 = (TextView) v3;
                                t3.setText(finalHtml);
                            }
                        });

                    }
                } else {
                    //On remplace le textview par l'erreur
                    View v3 = findViewById(R.id.resultatHtml);
                    TextView t3 = (TextView) v3;
                    t3.setText("Mauvaise URL");
                }
            }


            private String readFile(File f) throws IOException {

                FileInputStream is = new FileInputStream(f);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String s = br.readLine();

                String rs = s;
                while (s != null) {
                    rs = rs + s;
                    s = br.readLine();
                }
                return rs;
            }

            private void writeFile(File f, String s) throws IOException {
                try {
                    FileOutputStream isw = null; // je place ma tete de lecture, je vais lire les octets
                    isw = new FileOutputStream(f);
                    OutputStreamWriter isrw = new OutputStreamWriter(isw); // va permettre au Buffer de lire (il traduit)
                    BufferedWriter brw = new BufferedWriter(isrw); // Je vais le lire

                    try {
                        brw.write(s);// je vais lire telle ligne traduit des octets en chaine de caractères
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        brw.close();// je fermer le fichier
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }

            private String readUrl(URL u) throws IOException {
                URLConnection c = u.openConnection();
                InputStream cis = c.getInputStream();
                InputStreamReader cisr = new InputStreamReader(cis);
                BufferedReader cbr = new BufferedReader(cisr);
                String sc = cbr.readLine();
                String rsc = sc;
                while (sc != null) {
                    rsc = rsc + sc;
                    sc = cbr.readLine();
                }
                return rsc;
            }
        });
    t.start();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
