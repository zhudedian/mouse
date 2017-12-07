package com.ider.mouse.db;



/**
 * Created by Eric on 2017/10/16.
 */

public class App {
    private String packageName;

    private String labelName;

    private String type;

    private String createTime;
    private int versionCode;
    private String versionName;

    public App(String packageName, String labelName){
        this.packageName = packageName;
        this.labelName = labelName;
    }
    public App(String packageName, String labelName, String type,int versionCode,String versionName){
        this.packageName = packageName;
        this.labelName = labelName;
        this.type = type;
        this.versionCode = versionCode;
        this.versionName = versionName;
    }

    public void setPackageName(String name){
        this.packageName = name;
    }

    public String getPackageName(){
        return packageName;
    }

    public void setLabelName(String name){
        this.labelName = name;
    }
    public String getLabelName(){
        return labelName;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public void setVersionCode(int versionCode){
        this.versionCode = versionCode;
    }
    public int getVersionCode(){
        return versionCode;
    }
    public void setVersionName(String versionName){
        this.versionName = versionName;
    }
    public String getVersionName(){
        return versionName;
    }
    public void setCreateTime(String createTime){
        this.createTime = createTime;
    }
    public String getCreateTime(){
        return createTime;
    }

    @Override
    public boolean equals(Object object){
        if (object instanceof App){
            App app= (App) object;
            if (app.packageName.equals(this.packageName)){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    @Override
    public int hashCode(){
        return 2;
    }
}
