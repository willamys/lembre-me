package telas;

import java.util.ArrayList;
import com.src.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import modelo.LembreteVO;

 /**@author Willamys Araujo
 **Generate for Jacroid**/

public class LembreteAdapter extends ArrayAdapter<String> {
	
	private final ArrayList<LembreteVO> lembretes;
	private final ArrayList<String> keysLembretes;
	private final Context context;
	
	public LembreteAdapter(ArrayList<String> keysLembretes, ArrayList<LembreteVO> lembretes, Context context) {
		super(context, R.layout.adapter, R.id.TextViewKey,keysLembretes);
		this.keysLembretes = keysLembretes;
		this.lembretes = lembretes;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return keysLembretes.size();
	}

	@Override
	public String getItem(int position) {
		return keysLembretes.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(this.context).inflate(R.layout.adapter, parent, false);
		String key = keysLembretes.get(position);
		TextView nome = (TextView) view.findViewById(R.id.TextViewKey);
		nome.setText(String.valueOf(key));
		return view;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		//LembreteVO lembrete = lembretes.get(position);
		String key = keysLembretes.get(position);
		TextView label = new TextView(context);
		label.setTextSize(20f);
		label.setPadding(10,10,10,10);
		label.setText(key);
		//label.setText(String.valueOf(lembrete.getKeyLembrete()));
		return label;
	}
}
