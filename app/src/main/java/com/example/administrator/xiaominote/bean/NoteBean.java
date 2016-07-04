package com.example.administrator.xiaominote.bean;

/**
 * Created by Administrator on 2016/6/24.
 */
public class NoteBean {
    public String content;//内容
    public String date;//日期
    public boolean isDirectory;//是否是目录
    public String dirName;
    public String count;
    public boolean isCheck;
    public String colortype;
    public boolean isEidtable;
    public NoteBean(String content){
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public String getColortype() {
        return colortype;
    }

    public void setColortype(String colortype) {
        this.colortype = colortype;
    }
}
