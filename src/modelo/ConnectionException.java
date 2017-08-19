package modelo;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

public class ConnectionException {

	
	public static void erro(Context con, String Exception)
	{
		Dialog diag=new Dialog(con);
		diag.setTitle("Erro");
		TextView txt=new TextView(con);
		txt.setText(Exception);
		diag.setContentView(txt);
		diag.show();
	}
}