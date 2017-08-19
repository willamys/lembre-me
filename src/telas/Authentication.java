package telas;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.src.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import util.FirebaseUtil;

public class Authentication extends Activity implements
View.OnClickListener {

	private static final String TAG = "Authentication";

	private TextView mStatusTextView;
	private TextView mDetailTextView;
	private EditText mEmailField;
	private EditText mPasswordField;

	// [START declare_auth]
	private FirebaseAuth mAuth;
	// [END declare_auth]

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.authentication);

		// Views
		mStatusTextView = (TextView) findViewById(R.id.status);
		mDetailTextView = (TextView) findViewById(R.id.detail);
		mEmailField = (EditText) findViewById(R.id.field_email);
		mPasswordField = (EditText) findViewById(R.id.field_password);

		// Buttons
		findViewById(R.id.email_sign_in_button).setOnClickListener(this);
		findViewById(R.id.email_create_account_button).setOnClickListener(this);
		findViewById(R.id.sign_out_button).setOnClickListener(this);
		findViewById(R.id.tela_inicial).setOnClickListener(this);

		// [START initialize_auth]
		FirebaseUtil.getInstance().getInit(getApplicationContext());
		mAuth = FirebaseAuth.getInstance();
		// [END initialize_auth]
	}

	// [START on_start_check_user]
	@Override
	public void onStart() {
		super.onStart();
		// Check if user is signed in (non-null) and update UI accordingly.
		FirebaseUser currentUser = mAuth.getCurrentUser();
		updateUI(currentUser);
	}
	// [END on_start_check_user]

	private void createAccount(String email, String password) {
		Log.d(TAG, "createAccount:" + email);
		if (!validateForm()) {
			return;
		}

		//showProgressDialog();

		// [START create_user_with_email]
		mAuth.createUserWithEmailAndPassword(email, password)
		.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(Task<AuthResult> task) {
				if (task.isSuccessful()) {
					// Sign in success, update UI with the signed-in user's information
					Log.d(TAG, "createUserWithEmail:success");
					FirebaseUser user = mAuth.getCurrentUser();
					updateUI(user);
					Log.w(TAG, "signInWithEmail:failure", task.getException());
					Toast.makeText(Authentication.this, "Conta criada com sucesso.",
							Toast.LENGTH_SHORT).show();
				} else {
					// If sign in fails, display a message to the user.
					Log.w(TAG, "createUserWithEmail:failure", task.getException());
					Toast.makeText(Authentication.this, "Falha na autenticação. Tente novamente.",
							Toast.LENGTH_SHORT).show();
					updateUI(null);
				}

				// [START_EXCLUDE]
				// hideProgressDialog();
				// [END_EXCLUDE]
			}
		});
		// [END create_user_with_email]
	}

	private void signIn(String email, String password) {
		Log.d(TAG, "signIn:" + email);
		if (!validateForm()) {
			return;
		}

		// showProgressDialog();

		// [START sign_in_with_email]
		mAuth.signInWithEmailAndPassword(email, password)
		.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(Task<AuthResult> task) {
				if (task.isSuccessful()) {
					// Sign in success, update UI with the signed-in user's information
					Log.d(TAG, "signInWithEmail:success");
					FirebaseUser user = mAuth.getCurrentUser();
					updateUI(user);
					Log.w(TAG, "signInWithEmail:failure", task.getException());
					Toast.makeText(Authentication.this, "Autenticado com sucesso.",
							Toast.LENGTH_SHORT).show();
					initMain();
				} else {
					// If sign in fails, display a message to the user.
					Log.w(TAG, "signInWithEmail:failure", task.getException());
					Toast.makeText(Authentication.this, "Falha na autenticação. Tente novamente.",
							Toast.LENGTH_SHORT).show();
					updateUI(null);
				}

				// [START_EXCLUDE]
				if (!task.isSuccessful()) {
					mStatusTextView.setText("Falha na autenticação. Tente novamente.");
				}
				//hideProgressDialog();
				// [END_EXCLUDE]
			}
		});
		// [END sign_in_with_email]
	}

	private void signOut() {
		mAuth.signOut();
		updateUI(null);
	}

	private boolean validateForm() {
		boolean valid = true;

		String email = mEmailField.getText().toString();
		if (TextUtils.isEmpty(email)) {
			mEmailField.setError("Required.");
			valid = false;
		} else {
			mEmailField.setError(null);
		}

		String password = mPasswordField.getText().toString();
		if (TextUtils.isEmpty(password)) {
			mPasswordField.setError("Required.");
			valid = false;
		} else {
			mPasswordField.setError(null);
		}

		return valid;
	}

	private void updateUI(FirebaseUser user) {
		//hideProgressDialog();
		if (user != null) {
			mStatusTextView.setText("Bem Vindo, "+ user.getEmail());
			//mDetailTextView.setText(user.getUid());

			findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
			findViewById(R.id.email_password_fields).setVisibility(View.GONE);
			findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);

			//findViewById(R.id.verify_email_button).setEnabled();
		} else {
			mStatusTextView.setText("não autenticado");
			mDetailTextView.setText(null);

			findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
			findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
			findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.email_create_account_button) {
			createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
		} else if (i == R.id.email_sign_in_button) {
			signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
		} else if (i == R.id.sign_out_button) {
			signOut();
		} else if (i == R.id.tela_inicial) {
			initMain();
		}
	}

	private void initMain() {
		Intent i = new Intent(Authentication.this, Main.class);
		startActivity(i);
	}
}