package org.chamedu.stifco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import network.CustomOnItemSelectedListener;
import network.OnResultListener;
import network.RestClient;

import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class Rechercher extends Activity implements ViewSwitcher.ViewFactory,View.OnClickListener {
	
	Button soumettre;
	Spinner mySpinner;
	
	// Varaibles pour la lecture du flux Json
	private String jsonString;
	JSONObject jsonResponse;
	JSONArray arrayJson;
	AutoCompleteTextView tvGareAuto;
	ArrayList<String> items = new ArrayList<String>();
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rechercher);
		
		mySpinner = (Spinner) findViewById(R.id.mois);
        List<String> list = new ArrayList<String>();

        //and add this to your layout.
    	TextView text = (TextView) findViewById(R.id.titre_mois);
    	text.setText("Mois : ");

        list.add("janvier");
        list.add("fevrier");
        list.add("mars");
        list.add("avril");
        list.add("mai");
        list.add("juin");
        list.add("juillet");
        list.add("aout");
        list.add("septembre");
        list.add("octobre");
        list.add("novembre");
        list.add("decembre");
        
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
         
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		         
		mySpinner.setAdapter(dataAdapter);
		
		 // Spinner item selection Listener 
        addListenerOnSpinnerItemSelection();
        
		// Traitement du textView en autocomplétion à partir de la source Json
		jsonString = lireJSON();

		try {
			jsonResponse = new JSONObject(jsonString);
			// Création du tableau général à partir d'un JSONObject
			JSONArray jsonArray = jsonResponse.getJSONArray("gares");

			// Pour chaque élément du tableau
			for (int i = 0; i < jsonArray.length(); i++) {

			// Création d'un tableau élément à partir d'un JSONObject
			JSONObject jsonObj = jsonArray.getJSONObject(i);

			// Récupération à partir d'un JSONObject nommé
			JSONObject fields  = jsonObj.getJSONObject("fields");

			// Récupération de l'item qui nous intéresse
			String gare = fields.getString("nom_de_la_gare");

			// Ajout dans l'ArrayList
			items.add(gare);		
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, items);
			tvGareAuto = (AutoCompleteTextView)findViewById(R.id.actvGare);
			tvGareAuto.setAdapter(adapter);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Listener sur le bouton d'envoi
		soumettre = (Button)findViewById(R.id.btRechercher);
		soumettre.setOnClickListener(this);

	}
	
	// Add spinner data
    
    public void addListenerOnSpinnerItemSelection(){
                mySpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

	private void doOnTrueResult( String json ) {
		if ( json.equals("recherche_vide")) {
			Toast.makeText(Rechercher.this, "Pas de propositions", Toast.LENGTH_LONG).show();
			finish();
		} else {
			Intent iResultat = new Intent(this,Resultat.class);
			Toast.makeText(Rechercher.this, "Recherche en cours", Toast.LENGTH_LONG).show();
			iResultat.putExtra("value", json);
			this.startActivity( iResultat );
		}
    }
		
	private void setDialogOnClickListener(int buttonId, final int dialogId) {
		Button b = (Button)findViewById(buttonId);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(dialogId);
			}
		});
	}
	
	public void onClick(View v) {
		
		if ( v == soumettre ) {
						
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("gare",""+tvGareAuto.getText()));
			nameValuePairs.add(new BasicNameValuePair("mois",""+mySpinner.getSelectedItem().toString()));
						
			try {				
				RestClient.doPost("/recherche.php", nameValuePairs, new OnResultListener() {					
					@Override
					public void onResult(String json) {
						doOnTrueResult(json);			
					}
				});
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	
	public String lireJSON() {
		InputStream is = getResources().openRawResource(R.raw.gares);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return writer.toString();
	}
	
	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		return null;
	}


}
