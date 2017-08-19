package telas;

import modelo.LembreteVO;
import gps.Maps;
import camera.CameraActivity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

 /**@author Willamys Araujo
 **Generate for Jacroid**/

public class LembreteCadastrarActivity extends Activity{

	//Firebase
	private FirebaseDatabase database;
	private DatabaseReference reference;
	private String LEMBRETES_CHILD = "lembretes";
	
	private static final String tag = "LembreteCadAct";
	private EditText assuntoField;
	private EditText localizacaoField;
	private EditText descricaoField;
	private EditText fotoAuxField; 
	private byte[] imgData;
	private int PICK_IMAGE_REQUEST = 1;
	private ImageView fotoField;
	private String message;
	private long idB;
	private LembreteVO objLembrete;
	//objeto criado com o intuito de obter os dados alterados
	private LembreteVO objLembreteInserir;

	private void initControls(){
		assuntoField = (EditText) findViewById(R.id.EditTextassunto); 
		localizacaoField = (EditText) findViewById(R.id.EditTextlocalizacao); 
		descricaoField = (EditText) findViewById(R.id.EditTextdescricao); 
		fotoField =  (ImageView) findViewById(R.id.ImageViewfoto);
		fotoAuxField = (EditText) findViewById(R.id.EditTextfoto);
	}

	public LembreteCadastrarActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lembretecadastraractivity);
		initControls();
		
		objLembreteInserir = new LembreteVO();
		
		FirebaseUtil.getInstance().getInit(getApplicationContext());
		localizacaoField.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(LembreteCadastrarActivity.this, Maps.class);
				startActivityForResult(i, 1);
			}
		});
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
							Intent i = new Intent(LembreteCadastrarActivity.this, CameraActivity.class);
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

		Button botao = (Button) findViewById(R.id.ButtonSendFeedback);
		botao.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				insert();
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
	
	public void insert(){
		try {
			Intent data = new Intent();

			Log.i(tag,"onClick invoked.");

			/*******INSERCAO NO BD********/
			objLembreteInserir.assunto = assuntoField.getText().toString();
			objLembreteInserir.localizacao = localizacaoField.getText().toString();
			objLembreteInserir.descricao = descricaoField.getText().toString();
			objLembreteInserir.foto = fotoAuxField.getText().toString();

			if(validation(objLembreteInserir)){
			
				FirebaseUtil futil = new FirebaseUtil(); 
				futil.getInit(getApplicationContext());
				StorageReference mStorageRef = futil.getStorageReference();
				FirebaseAuth mAuth = futil.getFirebaseAuth();
				long idB1 = FirebaseUtil.getInstance().upload(getApplicationContext(), getAppName(), fotoAuxField.getText().toString(), imgData, mStorageRef, mAuth);
				Log.i(tag, "The insert have a return equal ["+ idB1 +"]");
			
				idB = FirebaseUtil.getInstance().insert(getApplicationContext(), LEMBRETES_CHILD, objLembreteInserir);	
				Log.i(tag, "The insert have a return equal ["+ idB +"]");
							
				if(idB1 < 1 &&	idB < 1){
					message = "Nao foi possivel efetuar o cadastro.";
				}else{
					message = "Cadastro efetuado com sucesso.";
				}
				data.putExtra("valor", message);
				setResult(2,data);
				finish();
			}else{
				message = "Complete todos os campos.Tente novamente.";
			}
		}catch (Exception e) {
			ConnectionException.erro(LembreteCadastrarActivity.this, "Erro ao inserir.\n Erro:\n " + e.toString());
		}	
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
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*** ATUALIZAR VALOR DO EditText QUE RECEBERA A LOCALIZACAO**/
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
	public void onBackPressed(){
		Intent data = new Intent();
		data.putExtra("valor", "");
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
