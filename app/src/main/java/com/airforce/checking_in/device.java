package com.airforce.checking_in;

import cn.bmob.v3.BmobObject;

/**
 * Created by Blue on 2016-02-19.
 */
public class device extends BmobObject {
    public Integer device_id;
    public Integer task_id;
    public String device_mac;
    public String device_name;
    public boolean checked=false;

    public boolean isChecked(){
        return this.checked;
    }
}
