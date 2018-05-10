package com.leedian.oviewremote.model;

import java.util.ArrayList;

/**
 * Created by francoliu on 2017/2/24.
 */

public class CameraShootingParam {

    String expMode;
    String expFNumber;
    String expISO;
    String expShortcut;

    public String getCamLensZoom() {
        return camLensZoom;
    }

    public void setCamLensZoom(String camLensZoom) {
        this.camLensZoom = camLensZoom;
    }

    String camLensZoom;
    String expExposureCompensation;
    ArrayList camAvailableIsoSpeedRate;
    ArrayList camAvailableFNumber;
    ArrayList camAvailableShortcut;
    ArrayList camAvailableExposureCompensation;

    public ArrayList getCamAvailableLensZoom() {
        return camAvailableLensZoom;
    }

    public void setCamAvailableLensZoom(ArrayList camAvailableLensZoom) {
        this.camAvailableLensZoom = camAvailableLensZoom;
    }

    ArrayList camAvailableLensZoom;


    public String getExpExposureCompensation() {
        return expExposureCompensation;
    }

    public void setExpExposureCompensation(String expExposureCompensation) {
        this.expExposureCompensation = expExposureCompensation;
    }

    public ArrayList getCamAvailableExposureCompensation() {
        return camAvailableExposureCompensation;
    }

    public void setCamAvailableExposureCompensation(ArrayList camAvailableExposureCompensation) {
        this.camAvailableExposureCompensation = camAvailableExposureCompensation;
    }

    public ArrayList getCamAvailableFNumber() {
        return camAvailableFNumber;
    }

    public void setCamAvailableFNumber(ArrayList camAvailableFNumber) {
        this.camAvailableFNumber = camAvailableFNumber;
    }

    public ArrayList getCamAvailableShortcut() {
        return camAvailableShortcut;
    }

    public void setCamAvailableShortcut(ArrayList camAvailableShortcut) {
        this.camAvailableShortcut = camAvailableShortcut;
    }

    public String getExpMode() {
        return expMode;
    }

    public void setExpMode(String mode) {
        this.expMode = mode;
    }

    public String getExpFNumber() {
        return expFNumber;
    }

    public void setExpFNumber(String expFNumber) {
        this.expFNumber = expFNumber;
    }

    public String getExpISO() {
        return expISO;
    }

    public void setExpISO(String iso) {
        this.expISO = iso;
    }

    public String getExpShortcut() {
        return expShortcut;
    }

    public ArrayList getCamAvailableIsoSpeedRate() {
        return camAvailableIsoSpeedRate;
    }

    public void setCamAvailableIsoSpeedRate(ArrayList camAvailableIsoSpeedRate) {
        this.camAvailableIsoSpeedRate = camAvailableIsoSpeedRate;
    }

    public String getFNumber() {
        return expFNumber;
    }

    public void setFNumber(String FNumber) {
        this.expFNumber = FNumber;
    }

    public void setShortcut(String shortcut) {
        this.expShortcut = shortcut;
    }
}
