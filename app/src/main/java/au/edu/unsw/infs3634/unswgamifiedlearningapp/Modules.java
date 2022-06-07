package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import java.util.ArrayList;

import javax.xml.xpath.XPath;

public class Modules {

    private int moduleID, moduleXP, previewImg;
    private String moduleTitle, url;
    private ArrayList<String> categoryTags;

    public Modules(int moduleID, String moduleTitle, String url,  int moduleXP, int previewImg) {
        this.moduleID = moduleID;
        this.moduleTitle = moduleTitle;
        this.url = url;
        this.moduleXP = moduleXP;
        this.previewImg = previewImg;
    }


    public int getModuleID() {
        return moduleID;
    }

    public void setModuleID(int moduleID) {
        this.moduleID = moduleID;
    }

    public String getModuleTitle() {
        return moduleTitle;
    }

    public void setModuleTitle(String moduleTitle) {
        this.moduleTitle = moduleTitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) { this.url = url; }

    public int getModuleXP() {
        return moduleXP;
    }

    public void setModuleXP(int moduleXP) {
        this.moduleXP = moduleXP;
    }

    public int getPreviewImg() {
        return previewImg;
    }

    public void setPreviewImg(int previewImg) {
        this.previewImg = previewImg;
    }
}
