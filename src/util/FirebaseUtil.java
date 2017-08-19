package util;

import java.util.Map;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.content.Context;

 /**@author Willamys Araujo
 **Generate for Jacroid**/

public class FirebaseUtil<T> implements Crud<T> {
	private FirebaseDatabase firebaseDatabase = null;
	private static FirebaseUtil firebaseUtil = null;
	private StorageReference storageReference = null;
	private FirebaseAuth firebaseauth = null; 
	private static FirebaseApp firebaseApp = null;
	long result = 0;
    byte[] localFile = null;
    public FirebaseUtil() {}

    public static FirebaseUtil getInstance() {
        if(firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
        }
        return firebaseUtil;
    }
	public void getInit(Context context){
		if(firebaseApp == null ){
			FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
					.setDatabaseUrl("https://fir-jacroid-99483.firebaseio.com/")
					.setApiKey("AIzaSyDWArgmMRLanjil2UH5WJBzomBTqwdUUZw")
					.setStorageBucket("fir-jacroid-99483.appspot.com")
					.setApplicationId("fir-jacroid-99483").build();;
			firebaseApp = FirebaseApp.initializeApp(context, firebaseOptions);
			
			firebaseDatabase = FirebaseDatabase.getInstance();
			firebaseDatabase.setPersistenceEnabled(true);
			storageReference = FirebaseStorage.getInstance().getReference();
 
		}else{
			firebaseDatabase = FirebaseDatabase.getInstance();
			storageReference = FirebaseStorage.getInstance().getReference();
 
		}
	}
	
	public FirebaseDatabase getFirebaseDatabase(){
		return firebaseDatabase;
	}

	public StorageReference getStorageReference() {
		return storageReference;
	}

	public FirebaseAuth getFirebaseAuth() {
		firebaseauth = FirebaseAuth.getInstance();
		return firebaseauth;
	}
    @Override
    public long insert(Context context, String reference, T vo) {
        if(vo == null){
            return -1;
        }else{
        
            getFirebaseDatabase().getReference(reference).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(vo);
            return 1;
        }
    } 
    @Override
    public long update(Context context, String reference, DatabaseReference dtReference, Map<String, Object> values) {
        if(values == null){
            return -1;
        }else{
            dtReference.updateChildren(values);
            return 1;
        }
    }

    @Override
    public long delete(Context context, String reference, DatabaseReference dtReference, T vo) {
        if(vo == null){
            return -1;
        }else{
            dtReference.removeValue();
            return 1;
        }
    }
    @Override
	public long upload(final Context context, String appName,String nameImage, byte[] imgData, 
			StorageReference storageReference, FirebaseAuth mAuth) {
  
		StorageReference imgRef = storageReference.child("images/"+nameImage);

		imgRef.putBytes(imgData)
		.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
				result = 1;
			}
		})
		.addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(Exception exception) {
				result = -1;
				//Log.e("ds", "FailureUpload", exception);
				//modelo.ConnectionException.erro(context, exception.toString());
			}
		});
		return result;
	}

	@Override
	public byte[] download(final Context context, String appName, String nameImage, byte[] imgData,
			StorageReference storageReference, FirebaseAuth mAuth) {
  
		storageReference.child("images/"+nameImage).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
			    @Override
			    public void onSuccess(byte[] bytes) {
			        localFile = bytes;
			    }
			}).addOnFailureListener(new OnFailureListener() {
			    @Override
			    public void onFailure(Exception exception) {
			       localFile = null;
			       //Log.e("ds", "FailureDownload", exception);
			       //modelo.ConnectionException.erro(context, exception.toString());
			    }
			});
		return localFile;
	}
}
