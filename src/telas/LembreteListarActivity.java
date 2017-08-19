package telas;

import java.util.ArrayList;
import modelo.LembreteVO;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import java.util.ArrayList;
import util.FirebaseUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.src.android.R;

 /**@author Willamys Araujo
 **Generate for Jacroid**/

public class LembreteListarActivity extends Activity{

	//Firebase
	private FirebaseDatabase database;
	
	private LinearLayoutManager manager;
	private ImageButton floatButton;
	private DatabaseReference reference;
	private String LEMBRETES_CHILD = "lembretes";
	private static final String tag = "LembreteListAct";
	private ArrayList<LembreteVO> arrayLembrete = new ArrayList<>();
	private ArrayList<String> keys = new ArrayList<>();
	private ListView rvLembrete;
	private FirebaseListAdapter<LembreteVO> adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lembretelistaractivity);
		rvLembrete = (ListView) findViewById(R.id.rvLembrete);
		manager = new LinearLayoutManager(this);
		manager.setReverseLayout(true);
		manager.setStackFromEnd(true);
		
		FirebaseUtil.getInstance().getInit(getApplicationContext());
		reference = FirebaseUtil.getInstance().getFirebaseDatabase().getReference(LEMBRETES_CHILD).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
		adapter = new FirebaseListAdapter<LembreteVO>(this,LembreteVO.class, R.layout.item, reference) {
			@Override
			protected void populateView(final View view, LembreteVO model, int position) {
				final LembreteVO lembreteVO = model;
				
				final DatabaseReference ref = getRef(position);
				final String key = ref.getKey();
				final TextView tvKey = (TextView) view.findViewById(R.id.key);
				tvKey.setText(model.getDescricao());
				final ImageButton imgbEdit = (ImageButton) view.findViewById(R.id.edit);
				imgbEdit.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupMenu popupMenu = new PopupMenu(getApplicationContext(), imgbEdit);
						popupMenu.inflate(R.menu.listmenu);
						popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem item) {
								switch (item.getItemId()) {
									case R.id.item_edit:
										Intent i = new Intent(LembreteListarActivity.this, LembreteAlterarActivity.class);
										
										Log.i(tag, "on click " + key);
										i.putExtra("valor", key);
										startActivityForResult(i, 1);
										break;
									case R.id.item_delete:
										delete(ref, lembreteVO);
										break;
									default:
										break;
								}
								return false;
							}
						});
						popupMenu.show();
					}
				});
			}
		};
		
		rvLembrete.setAdapter(adapter);
		
		floatButton = (ImageButton) findViewById(R.id.add);
		floatButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(LembreteListarActivity.this, LembreteCadastrarActivity.class);
				startActivityForResult(i, 1);
			}
		});
	
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
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public boolean delete(final DatabaseReference reference, final LembreteVO lembreteVO){
		new AlertDialog.Builder(this).setTitle(this.getResources()
				.getString(R.string.app_name)).setMessage(
						"Voce realmente deseja deletar?").setPositiveButton("SIM", 
								new android.content.DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface arg0,
									int arg1) {
								String message = "";
								long ret = FirebaseUtil.getInstance().delete(getApplicationContext(),LEMBRETES_CHILD, reference, lembreteVO);
								if( ret != -1 && ret != 0){
									message = "Delete efetuado com sucesso.";
								}else{
									message = "Nao foi possivel efetuar o delete.";
								}
								Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
							}
						}).setNegativeButton("No", new android.content.DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();
		return false;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Toast.makeText(this,data.getExtras().getString("valor"), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onBackPressed() 
	{
		Intent data = new Intent();
		data.putExtra("valor", "");
		setResult(2,data);
		finish();
	}
}
