package com.airforce.checking_in;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by Blue on 2016-02-19.
 */
public class User extends BmobUser{
    private Integer user_id;

    public Integer getUID(){
        return this.user_id;
    }

    public void setUID(Integer id){
        this.user_id = id;
    }
}
