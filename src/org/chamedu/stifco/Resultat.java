package org.chamedu.stifco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class Resultat extends Activity implements ViewSwitcher.ViewFactory,
		View.OnClickListener {
	private String json;
	JSONObject jsonResponse;
	JSONArray jsonArray;
	ArrayList<Map<String, String>> items = new ArrayList<Map<String, String>>();
	private ListView liste;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resultat);
		liste = (ListView) findViewById(R.id.list);

		Intent intent = getIntent();
		// On suppose que tu as mis un String dans l'Intent via le putExtra()
		json = (String) getIntent().getExtras().get("value");

		TextView text = (TextView) findViewById(R.id.resultat);
		text.setText("Résultat : ");

		try {
			jsonResponse = new JSONObject(json);
			// Création du tableau général à partir d'un JSONObject
			JSONArray jsonArray = jsonResponse.getJSONArray("propositions");

			// Pour chaque élément du tableau
			for (int i = 0; i < jsonArray.length(); i++) {

				// Création d'un tableau élément à  partir d'un JSONObject
				JSONObject jsonObj = jsonArray.getJSONObject(i);

				// Récupération de l'item qui nous intéresse
				String id = jsonObj.getString("id");
				String ville = jsonObj.getString("ville");
				String lieu = jsonObj.getString("lieu");
				String places = jsonObj.getString("places");
				String gare = jsonObj.getString("gare");

				// Ajout dans l'ArrayList
				Map<String, String> datum = new HashMap<String, String>(5);
			    datum.put("id", id);
			    datum.put("ville", ville);
			    datum.put("lieu", lieu);
			    datum.put("places", places);
			    datum.put("gare", gare);
			    items.add(datum);
			}
			
			SimpleAdapter adapter = new SimpleAdapter(this, items,
                    android.R.layout.simple_list_item_2,
                    new String[] {"id", "ville", "lieu", "places", "gare"}, 
                    new int[] {android.R.id.text1,
                            android.R.id.text2});
			liste.setAdapter(adapter);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		return null;
	}

}
