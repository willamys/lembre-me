package gps;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.src.android.R;

import java.util.ArrayList;
import java.util.List;
import modelo.LembreteVO;
import util.FirebaseUtil;

 /**@author Willamys Araujo
 **Generate for Jacroid**/

public class GpsMapsPointers extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    DatabaseReference reference;
    /***CRIANDO A LISTA DE PONTOS **/
	ArrayList<LembreteVO> lembreteList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        MapFragment mapFragment = MapFragment.newInstance();
		FragmentManager manager = getFragmentManager();
	    FragmentTransaction transaction = manager.beginTransaction();
	    transaction.add(R.id.map, mapFragment);           
	    transaction.commit();
	    mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
     	mMap = googleMap;
     	FirebaseUtil.getInstance().getInit(getApplicationContext());
		lembreteList = new ArrayList<LembreteVO>();
		reference = FirebaseUtil.getInstance().getFirebaseDatabase().getReference("lembretes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()) {
                	for(DataSnapshot msgSnapshot2: msgSnapshot.getChildren()){
                	lembreteList.add(msgSnapshot2.getValue(LembreteVO.class));
	                    String local = msgSnapshot2.getValue(LembreteVO.class).getLocalizacao();
	                    String latit =  local.substring(0,local.indexOf(","));
	                    String longi = local.substring(local.indexOf(",")+1, local.length()-1);
	                   	LatLng locate = new LatLng(Double.parseDouble(latit),Double.parseDouble(longi));
	                   	mMap.addMarker(new MarkerOptions().position(locate).snippet(msgSnapshot2.getValue(LembreteVO.class).getDescricao()).title("lembrete"));
	                   	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locate,18));
                	}
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
      
    }
}