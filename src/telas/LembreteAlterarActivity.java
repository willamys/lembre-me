package telas;

import modelo.LembreteVO;
import gps.Maps;
import camera.CameraActivity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import modelo.ConnectionException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.util.Vector;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import com.src.android.R;
import util.FirebaseUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
 /**@author Willamys Araujo
 **Generate for Jacroid**/

public class LembreteAlterarActivity extends Activity{

	private static final String tag = "LembreteAltAct";
	private FirebaseDatabase database;
 	private DatabaseReference reference;
 	private String LEMBRETES_CHILD =  "lembretes";

	private EditText assuntoField;
	private EditText localizacaoField;
	private EditText descricaoField;
	private EditText fotoAuxField; 
	private byte[] imgData;
	private int PICK_IMAGE_REQUEST = 1;
	private ImageView fotoField;
	private String message;
	private long idB;
	private long idB1 = 1; 
	private String valor;
	private LembreteVO objLembrete;
	//objeto criado com o intuito de obter os dados alterados
	private LembreteVO objLembreteInserir = new LembreteVO();

	private void initControls() {
		assuntoField = (EditText) findViewById(R.id.EditTextassunto); 
		localizacaoField = (EditText) findViewById(R.id.EditTextlocalizacao); 
		descricaoField = (EditText) findViewById(R.id.EditTextdescricao); 
		fotoField =  (ImageView) findViewById(R.id.ImageViewfoto);
		fotoAuxField = (EditText) findViewById(R.id.EditTextfoto);
}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lembretealteraractivity);
		initControls();
		//pegar valor passado como parametro
		Intent intr = getIntent();
		valor = intr.getStringExtra("valor");
		Log.i(tag, " Valor passado " + "[ " + valor + " ]");
		 localizacaoField.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(LembreteAlterarActivity.this, Maps.class);
				startActivityForResult(i, 1);
			}
		});
		
		objLembrete = new LembreteVO();
		fotoAuxField.setVisibility(android.view.View.INVISIBLE);
		fotoField.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PopupMenu popupMenu = new PopupMenu(getApplicationContext(), fotoField);
				popupMenu.inflate(R.menu.menuimage);
				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getItemId()) {
						case R.id.item_camera:
							Intent i = new Intent(LembreteAlterarActivity.this, CameraActivity.class);
							startActivityForResult(i, 1);
							break;
						case R.id.item_gallery:
							Intent intent = new Intent();
							// Show only images, no videos or anything else
							intent.setType("image/*");
							intent.setAction(Intent.ACTION_GET_CONTENT);
							// Always show the chooser (if there are multiple options available)
							startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
		FirebaseUtil.getInstance().getInit(getApplicationContext());
 
		reference = FirebaseUtil.getInstance().getFirebaseDatabase().getReference().child(LEMBRETES_CHILD).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(valor);
		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				objLembrete = dataSnapshot.getValue(LembreteVO.class);
			// INICIO adicionando os valores dos campos quando for feita a alteracao
				if(objLembrete != null){
					assuntoField.setText(String.valueOf(objLembrete.assunto));
					localizacaoField.setText(String.valueOf(objLembrete.localizacao));
					descricaoField.setText(String.valueOf(objLembrete.descricao));
					fotoAuxField.setText(String.valueOf(objLembrete.foto));
					
					StorageReference mStorageRef = FirebaseUtil.getInstance().getStorageReference().child("images/"+ String.valueOf(objLembrete.foto));
					FirebaseAuth mAuth = FirebaseUtil.getInstance().getFirebaseAuth();
					FirebaseUser user = mAuth.getCurrentUser();
					// Load the image using Glide
					Glide.with(getApplicationContext())
					        .using(new FirebaseImageLoader())
					        .load(mStorageRef)
					        .into(fotoField);
				}else{
					onBackPressed();
				}
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {}
		});

		Button botao = (Button) findViewById(R.id.ButtonSendFeedback);
		botao.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				update();
			}
		});

		Button botaoVoltar = (Button) findViewById(R.id.ButtonBack);
		botaoVoltar.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	protected boolean validation(LembreteVO objLembreteInserir) {
		String campos="";
 
		if(!(String.valueOf(objLembreteInserir.assunto).equals("")
 || String.valueOf(objLembreteInserir.localizacao).equals("")
 || String.valueOf(objLembreteInserir.descricao).equals("")
 || String.valueOf(objLembreteInserir.foto).equals("")
		)){
			return true;
		}
		else{
			if(String.valueOf(objLembreteInserir.assunto).equals("")){
			campos = campos + "- Assunto\n";}
			if(String.valueOf(objLembreteInserir.localizacao).equals("")){
			campos = campos + "- Localizacao\n";}
			if(String.valueOf(objLembreteInserir.descricao).equals("")){
			campos = campos + "- Descricao\n";}
			if(String.valueOf(objLembreteInserir.foto).equals("")){
			campos = campos + "- foto\n";}
			new AlertDialog.Builder(this).setTitle(this.getResources()
					.getString(R.string.app_name)).setMessage(
					"Os campos:\n" + campos + " esta(ao) vazios.\n" +
					"Complete todos os campos. Tente novamente.")
					.setPositiveButton("continue", 
							new android.content.DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0,
								int arg1) {
						}
					}).show();
			return false;
		}
	}

	private void update(){
	try {
		Intent data = new Intent();
		Log.i(tag,"onClick invoked.");
		/*******INSERCAO NO BD********/
			objLembreteInserir.assunto = assuntoField.getText().toString();
			objLembreteInserir.localizacao = localizacaoField.getText().toString();
			objLembreteInserir.descricao = descricaoField.getText().toString();
			objLembreteInserir.foto = fotoAuxField.getText().toString();

	if(validation(objLembreteInserir)){
			if(imgData != null){
				FirebaseUtil futil = new FirebaseUtil(); 
				futil.getInit(getApplicationContext());
				StorageReference mStorageRef = futil.getStorageReference();
				FirebaseAuth mAuth = futil.getFirebaseAuth();
				idB1 = FirebaseUtil.getInstance().upload(getApplicationContext(), getAppName(), fotoAuxField.getText().toString(), imgData, mStorageRef, mAuth);
				Log.i(tag, "The update have a return equal ["+ idB1 +"]");
			}
			
			idB = FirebaseUtil.getInstance().update(getApplicationContext(),LEMBRETES_CHILD, reference, objLembreteInserir.toMap());
			
			Log.i(tag, "The update have a return equal ["+ idB +"]");

			if(idB1 < 1 &&	idB < 1){
				message = "Nao foi possivel efetuar a alteracao.";
			}else{
				message = "Alteracao efetuada com sucesso.";
			}
			data.putExtra("valor", message);
			setResult(2,data);
			finish();
		}else{
			message = "Complete todos os campos. Tente novamente.";
		}
		} catch (Exception e) {
				ConnectionException.erro(this, e.toString());
			}		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if((data != null) && ( data.hasExtra("gps") && !data.getExtras().getString("gps").equals(""))){
				String gps = data.getExtras().getString("gps");
				localizacaoField.setText(gps);
		}
		if((data != null) && (data.hasExtra("camera") && !data.getExtras().getString("camera").equals(""))){
				String camera = data.getExtras().getString("camera");
				File file = new File(camera);
				Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				imgData = baos.toByteArray();
				fotoField.setImageBitmap(bitmap);
				String nameImage = "IMG_"+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
				fotoAuxField.setText(nameImage);
		}
		if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
			Uri uri = data.getData();
			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				imgData = baos.toByteArray();
				fotoField.setImageBitmap(bitmap);
				String nameImage = "IMG_"+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
				fotoAuxField.setText(nameImage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onBackPressed() 
	{
		Intent data = new Intent();
		data.putExtra("valor", "voltar");
		setResult(2,data);
		finish();
	}
	public String getAppName(){
		PackageInfo pinfo =  null;
		String name = "";
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			name = pinfo.applicationInfo.loadLabel(getPackageManager()).toString();
		} catch (NameNotFoundException e) {
			Log.d(tag, "Error: "+ e.getMessage());
		}
		return name;
	}
}
