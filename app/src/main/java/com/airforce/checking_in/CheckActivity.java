package com.airforce.checking_in;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class CheckActivity extends AppCompatActivity {

//    public class device{
//        public String device_mac;
//        public String device_name;
//        public boolean device_check;
//        device(String device_mac,String device_name){
//            this.device_mac=device_mac;
//            this.device_name=device_name;
//            device_check=false;
//        }
//    }
//

    private ListView device_listview;
    private Integer task_id;
    private String task_name;
    private int selectDevice;
    private List<device> device_list_local = new ArrayList<device>();
    private List<device> device_list_cloud = new ArrayList<device>();

    private List<Map<String, String>> device_list_map = new ArrayList<Map<String, String>>();
    private BluetoothAdapter mBluetoothAdapter;
    ListAdapter adapter;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Toast.makeText(CheckActivity.this, "发现新设备", Toast.LENGTH_SHORT).show();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                System.out.println(device.getName());

                boolean isExist=false;
                for(device dev : device_list_local){
                    if(dev.device_mac.equals(device.getAddress())){
                        isExist=true;
                    }
                }
                if(isExist==false){
                    device d = new device();
                    d.device_mac = device.getAddress();
                    d.device_name = device.getName();
                    device_list_local.add(d);
                }

            }


            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                Toast.makeText(CheckActivity.this, "开始扫描考勤设备", Toast.LENGTH_LONG).show();
                device_list_local.clear();
                device_list_map.clear();
                device_listview.setAdapter(null);
            }
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Toast.makeText(CheckActivity.this, "扫描完成", Toast.LENGTH_LONG).show();
                        for(device dev : device_list_local){
                            for(device cdev:device_list_cloud){
                                if(cdev.device_mac.equals(dev.device_mac)){
                                    System.out.println(cdev.device_mac);
                                    dev.device_name = cdev.device_name;
                                    System.out.println(cdev.device_name);
                                    dev.checked = true;
                                    break;
                                }
                            }
                }
                for(device dev : device_list_local) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("device_name", dev.device_name);
                    map.put("device_mac", dev.device_mac);
                    device_list_map.add(map);
                }
                device_listview.setAdapter(adapter);

                for(int i = 0;i<device_listview.getCount();i++){
                    if(device_list_local.get(i).checked==true){
                        //device_listview.getChildAt(i).setBackgroundColor(Color.rgb(0, 187, 255));

                    }
                }
            }

        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "添加设备");
        menu.add(Menu.NONE, Menu.FIRST + 2, 2, "删除设备");
//        menu.add(Menu.NONE, Menu.FIRST + 3, 3, "--");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public boolean onContextItemSelected(MenuItem item) {
        final String name = device_list_map.get(selectDevice).get("device_name");
        final String mac = device_list_map.get(selectDevice).get("device_mac");
        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText input = new EditText(this);
                input.setText(name);
                builder.setTitle("设备名称");
                builder.setView(input);
                builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        device d = new device();
                        d.device_mac = mac;
                        d.device_name = input.getText().toString();
                        d.task_id = task_id;
                        d.save(CheckActivity.this, new SaveListener() {

                            @Override
                            public void onSuccess() {
                                Toast.makeText(CheckActivity.this, "添加成功", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure(int code, String arg0) {
                                // 添加失败
                            }
                        });
                    }
                });
                builder.show();



                break;
            case Menu.FIRST + 2:
                BmobQuery<device> query = new BmobQuery<device>();
                query.addWhereEqualTo("device_mac", mac);
                query.setLimit(1);
                query.findObjects(this, new FindListener<device>() {
                    @Override
                    public void onSuccess(List<device> object) {
                        // TODO Auto-generated method stub
                        for (device d : object) {
                            device dd = new device();
                            dd.setObjectId( d.getObjectId());
                            dd.delete(CheckActivity.this, new DeleteListener() {

                                @Override
                                public void onSuccess() {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(CheckActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    // TODO Auto-generated method stub
                                }
                            });
                      }
                    }
                    @Override
                    public void onError(int code, String msg) {
                        // TODO Auto-generated method stub
                        Toast.makeText(CheckActivity.this, "查询失败："+msg, Toast.LENGTH_LONG).show();
                    }
                });
                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);
    }


    public static List<String> removeDuplicate(List<String> list)
    {
        Set set = new LinkedHashSet<String>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,Menu.FIRST,1,"开始考勤");
        menu.add(Menu.NONE,Menu.FIRST+1,2,"上传日志");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Toast.makeText(this, String.valueOf(item.getItemId()), Toast.LENGTH_LONG).show();
        switch (item.getItemId()){
            case Menu.FIRST:
//                Toast.makeText(CheckActivity.this, "开始扫描考勤设备", Toast.LENGTH_LONG).show();
                mBluetoothAdapter.startDiscovery();
                break;
            case Menu.FIRST+1:
                Toast.makeText(CheckActivity.this, "正在上传考勤结果", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        task_id = Integer.valueOf(getIntent().getStringExtra("task_id"));
        System.out.println(task_id);
        task_name = getIntent().getStringExtra("task_name");
        device_listview = (ListView)findViewById(R.id.device_list);
        setTitle(task_name);

        BmobQuery<device> query = new BmobQuery<device>();
        query.addWhereEqualTo("task_id", task_id);
        query.setLimit(50);
        query.findObjects(this, new FindListener<device>() {
            @Override
            public void onSuccess(List<device> object) {
                // TODO Auto-generated method stub
//                Toast.makeText(CheckActivity.this, "查询成功：共"+object.size()+"条数据。", Toast.LENGTH_LONG).show();
                for (device d : object) {
                    //获得playerName的信息
//                    Toast.makeText(CheckActivity.this, d.device_name, Toast.LENGTH_SHORT).show();
                    device_list_cloud.add(d);
                    device_list_local.add(d);
                }
                mBluetoothAdapter.startDiscovery();
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                Toast.makeText(CheckActivity.this, "查询失败：" + msg, Toast.LENGTH_LONG).show();
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        IntentFilter mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, mFilter);
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, mFilter);
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver, mFilter);

        adapter = new SimpleAdapter(this, device_list_map, android.R.layout.simple_list_item_2,new String[]        { "device_name", "device_mac" }, new int[] { android.R.id.text1, android.R.id.text2 });
        device_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectDevice = position;
                registerForContextMenu(view);
                return false;
            }
        });
    }

}
