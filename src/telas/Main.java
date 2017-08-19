package telas;

import com.google.firebase.auth.FirebaseAuth;
import com.src.android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import gps.Maps;
import util.FirebaseUtil;
import gps.GpsMapsPointers;
import camera.CameraActivity;
import gps.Maps;
import gps.GpsMapsPointers;
import camera.CameraActivity;

 /**@author Willamys Araujo
 **Generate for Jacroid**/

public class Main extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		/**Botao para abrir o CRUD da Classe Lembrete*/
		Button buttonLembrete = (Button) findViewById(R.id.ButtonLembrete);
		buttonLembrete.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Main.this, LembreteListarActivity.class);
				startActivity(i);
			}
		});

		/**Button buttonMaps = (Button) findViewById(R.id.ButtonMaps);
		buttonMaps.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Main.this, Maps.class);
				startActivity(i);
			}
		});
		**/
		Button buttonGpsMapsPointers = (Button) findViewById(R.id.ButtonGpsMapsPointers);
		buttonGpsMapsPointers.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
			
				Intent i = new Intent(Main.this, GpsMapsPointers.class);
				startActivity(i);
			}
		});
		/**Button buttonCamera = (Button) findViewById(R.id.ButtonCamera);
		buttonCamera.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Main.this, CameraActivity.class);
				startActivity(i);
			}
		});
		**/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_info:
			return true;
		case R.id.sign_out:
			signOut();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void signOut() {
		FirebaseUtil.getInstance().getInit(getApplicationContext());
		FirebaseAuth mAuth = FirebaseAuth.getInstance();
    	mAuth.signOut();
    	Intent i = new Intent(Main.this,Authentication.class);
		startActivity(i);
	}
}
