package com.airforce.checking_in;

import cn.bmob.v3.BmobObject;

/**
 * Created by Blue on 2016-02-19.
 */
public class task extends BmobObject {
    public String task_name;
    public Integer task_id;
    public Integer user_id;

    public String getTaskName(){
        return this.task_name;
    }

    public Integer getTaskId(){
        return this.task_id;
    }
}

