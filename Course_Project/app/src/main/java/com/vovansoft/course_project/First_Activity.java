package com.vovansoft.course_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.telephony.SignalStrength;
import android.telephony.PhoneStateListener;


public class First_Activity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener {



    private TextView gsmLevel;
    private TextView wifiLevel;
    private TextView wifiSpeed;
    private TextView wifiStatus;
    private TextView wifiSSID;
    private CheckBox cbEnable;
    private WifiManager managerWiFi;
    private TextView text;

    //Вешаем слушалку
    private PhoneStateListener listener = new PhoneStateListener() {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            Integer strength = signalStrength.getGsmSignalStrength();
            //Если произошло событие, то закидываем
            gsmLevel.setText(strength.toString());
        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch(wifiState){
                case WifiManager.WIFI_STATE_ENABLING:
                    wifiStatus.setText("Wi-Fi state enabling");
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    wifiStatus.setText("Wi-Fi state enabled");
                    startMonitoringRssi();
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    wifiStatus.setText("Wi-Fi state disabling");
                    wifiSpeed.setText("");
                    wifiSSID.setText("");
                    wifiLevel.setText("");
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    wifiStatus.setText("Wi-Fi state disabled");
                    stopMonitoringRssi();
                    wifiSpeed.setText("");
                    wifiSSID.setText("");;
                    wifiLevel.setText("");

                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    wifiStatus.setText("Wi-Fi state unknown");
                    wifiSpeed.setText("");
                    wifiSSID.setText("");
                    wifiLevel.setText("");
                    break;
            }
        }
    };

    private BroadcastReceiver rssiReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            WifiInfo info = managerWiFi.getConnectionInfo();

           /* wifiSSID.append("\nChange signal in " + info.getSSID());
            wifiSSID.append("\n\tSignal level:\t" +
                    WifiManager.calculateSignalLevel(info.getRssi(), 5));
            wifiSSID.append("\n\tLink speed:\t" + info.getLinkSpeed() +
                    " " + WifiInfo.LINK_SPEED_UNITS);*/
            Integer wifiIntSignal = WifiManager.calculateSignalLevel(info.getRssi(), 100);
            Integer wifiIntSpeed = info.getLinkSpeed();
            wifiSSID.setText(info.getSSID());
            wifiSpeed.setText(wifiIntSpeed.toString() +  WifiInfo.LINK_SPEED_UNITS);
            wifiLevel.setText(wifiIntSignal.toString());
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_);
        //GSM network
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        gsmLevel = (TextView) findViewById(R.id.gsmLevel);
        wifiLevel = (TextView) findViewById(R.id.wifiLevel);
        wifiSpeed = (TextView)findViewById(R.id.wifiSpeed);
        wifiSSID =(TextView) findViewById(R.id.wifiSSID);
        wifiStatus = (TextView) findViewById(R.id.wifiStatus);
        manager.listen(listener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        //Wi-Fi network
        text = (TextView)findViewById(R.id.text);
        cbEnable = (CheckBox)findViewById(R.id.cbEnable);

        managerWiFi = (WifiManager)getSystemService(Context.WIFI_SERVICE);


        this.registerReceiver(this.receiver,
                new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

        cbEnable.setChecked(managerWiFi.isWifiEnabled());
        cbEnable.setOnCheckedChangeListener(this);



    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        managerWiFi.setWifiEnabled(isChecked);
    }

    private void startMonitoringRssi() {
        this.registerReceiver(rssiReceiver,
                new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    }

    private void stopMonitoringRssi() {
        if (this.rssiReceiver.isInitialStickyBroadcast())
            this.unregisterReceiver(rssiReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first_, menu);
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
