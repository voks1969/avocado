/**
 * Created by voks1969 on 5/27/2015.
 */

package sample.com.networkinfo;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.telephony.TelephonyManager;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {

    static final String TAG = " NetworkInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String txtString=null;

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiinfo = wm.getConnectionInfo();
        String ipAddress = Formatter.formatIpAddress(wifiinfo.getIpAddress());
        Log.d(TAG, " WIFI IP Address=" + ipAddress);
        txtString = " WIFI IP Address=" + ipAddress+ "\n";
        String macAddress =wifiinfo.getMacAddress();
        Log.d(TAG, " WIFI MAC Address=" + macAddress);
        txtString += " WIFI MAC Address=" + macAddress+ "\n";

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        int networkType = tm.getNetworkType();
        Log.d(TAG, " networkType =" + networkType);
        txtString += " networkType =" + networkType + "\n";

        final TextView textView=(TextView)findViewById(R.id.textView1);
        textView.setText(txtString);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
