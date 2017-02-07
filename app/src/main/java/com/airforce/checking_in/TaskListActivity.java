package com.airforce.checking_in;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Blue on 2016-02-13.
 */
public class TaskListActivity extends AppCompatActivity {
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        final ListView task_list = (ListView)findViewById(R.id.task_list);
        final List<String> adapterData = new ArrayList<String>();
        setTitle("考勤任务列表");
        final List<task> tasks = new ArrayList<task>();
        uid = getIntent().getStringExtra("uid"); //获取uid

        BmobQuery<task> query = new BmobQuery<task>();
        query.addWhereEqualTo("user_id", Integer.valueOf(uid));
        query.setLimit(50);
        query.findObjects(this, new FindListener<task>() {
            @Override
            public void onSuccess(List<task> object) {
                // TODO Auto-generated method stub
//                Toast.makeText(TaskListActivity.this, "查询成功：共" + object.size() + "条数据。", Toast.LENGTH_LONG).show();
                for (task t : object) {
//                    Toast.makeText(TaskListActivity.this, t.getTaskName(), Toast.LENGTH_LONG).show();
                    task new_task = new task();
                    new_task.task_id = t.getTaskId();
                    new_task.task_name = t.getTaskName();
                    tasks.add(new_task);
                    adapterData.add(t.getTaskName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(TaskListActivity.this,
                        android.R.layout.simple_list_item_1, adapterData);
                task_list.setAdapter(adapter);
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                Toast.makeText(TaskListActivity.this, "查询失败：" + msg, Toast.LENGTH_LONG).show();
            }
        });


        task_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TaskListActivity.this, CheckActivity.class);
                intent.putExtra("task_id", String.valueOf(tasks.get(position).getTaskId()));
                intent.putExtra("task_name", tasks.get(position).getTaskName());
                startActivity(intent);
            }
        });



    }
}
