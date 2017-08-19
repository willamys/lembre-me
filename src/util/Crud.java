package util;

import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import android.content.Context;

public interface Crud<T> {
	
	public long insert(Context context, String reference, T vo);
	public long update(Context context, String reference, DatabaseReference dtReference, Map<String, Object> values);
	public long delete(Context context, String reference, DatabaseReference dtReference, T vo);
	public long upload(Context context, String appName, String nameImage, byte[] imgData, StorageReference storageReference, FirebaseAuth mAuth);
	public byte[] download(Context context, String appName, String nameImage, byte[] imgData, StorageReference storageReference, FirebaseAuth mAuth);
	
}