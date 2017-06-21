package check_in.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.macbookpro.check_in.R;

import super_class.CheckInMapFragment;

public class AddSiteActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_site);

		getFragmentManager().beginTransaction().add(R.id.mapFrameLayout, CheckInMapFragment.newInstance()).commit();

	}
}
