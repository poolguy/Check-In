package super_class;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.macbookpro.check_in.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macbookpro on 6/19/17
 */

public class CheckInMapFragment extends MapFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	private GoogleMap map = null;
	private final LatLngBounds MAX_BOUNDS = new LatLngBounds(new LatLng(40.066675, -112.101143), new LatLng(40.826759, -111.575073));
	private final LatLngBounds CAMPUS_BOUNDS = new LatLngBounds(new LatLng(40.244190, -111.656089), new LatLng(40.255760, -111.643141));
	private LatLngBounds currentBounds = CAMPUS_BOUNDS;
	private final int REQUEST_ACCESS_FINE_LOCATION = 2;
	private Dialog googlePlayServicesDialog;
	private GoogleApiClient googleApiClient;
	private List<Marker> markers = new ArrayList<>();

	public GoogleMap getMap() {
		return map;
	}

	//	If you override this in fragments that extend ByuMapFragment, always call this (super.onMapReady(googleMap)) in the overridden function or things will break
	@Override
	public void onMapReady(GoogleMap googleMap) {
		this.map = googleMap;
		checkPermissionsAndSetMyLocation();
	}


	// Suppress new api warning for requestPermissions() method, because the following if statement will only evaluate to true if the api level is 23 or higher
	@SuppressLint("NewApi")
	protected void checkPermissionsAndSetMyLocation() {
		// This statement says: If the app does not already have permission to access the user's location
		// AND If the user hasn't already denied permission, THEN request permission to access the user's location.
		if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
			requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
		} else if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			map.setMyLocationEnabled(true);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
				map.setMyLocationEnabled(true);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		googleApiClient = new GoogleApiClient.Builder(getActivity(), this, this).addApi(LocationServices.API).build();
	}

	@Override
	public void onStart() {
		super.onStart();
		googleApiClient.connect();
	}

	@Override
	public void onStop() {
		super.onStop();
		googleApiClient.disconnect();
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		checkForGooglePlayServices(false);
		return inflater.inflate(R.layout.map_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// setup the map asynchronously
		MapFragment mapFragment = MapFragment.newInstance(getMapOptions());
		getFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (googlePlayServicesDialog == null) {
			checkForGooglePlayServices(true);
		}
	}

	private void checkForGooglePlayServices(boolean isCalledFromOnResume) {
		//Check to see if user has google play services enabled
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int status = apiAvailability.isGooglePlayServicesAvailable(getActivity());
		if (status != ConnectionResult.SUCCESS) {
			if (!isCalledFromOnResume) {
				googlePlayServicesDialog = apiAvailability.getErrorDialog(getActivity(), status, 0);
				googlePlayServicesDialog.show();
			} else {
				getActivity().onBackPressed();
			}
		}
	}

	protected GoogleMapOptions getMapOptions() {
		GoogleMapOptions googleMapOptions = new GoogleMapOptions();

//		googleMapOptions.mapType(setMapType());

		googleMapOptions.camera(setCameraPosition());

		return googleMapOptions;
	}

	protected CameraPosition setCameraPosition() {
//		Focus map on campus
		return new CameraPosition.Builder().target(CAMPUS_BOUNDS.getCenter()).zoom(14).build();
	}

//	protected int setMapType() {
////		Set map type
//		String normal = getString(R.string.map_view_style_pref_map_val);
//		String mapKey = getString(R.string.map_preference_key);
//		String prefVal = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(mapKey, normal);
//		if (prefVal.equals(normal)) {
//			return GoogleMap.MAP_TYPE_NORMAL;
//		} else if (prefVal.equals(getString(R.string.map_view_style_pref_hybrid_val))) {
//			return GoogleMap.MAP_TYPE_HYBRID;
//		} else if (prefVal.equals(getString(R.string.map_view_style_pref_satellite_val))) {
//			return GoogleMap.MAP_TYPE_SATELLITE;
//		} else {
//			PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(mapKey, normal).apply();
//			return GoogleMap.MAP_TYPE_NORMAL;
//		}
//	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}
}
