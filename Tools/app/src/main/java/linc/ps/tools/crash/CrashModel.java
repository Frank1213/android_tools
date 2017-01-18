package linc.ps.tools.crash;

/**
 * Created by Frank on 2016/12/23.
 * 全局异常捕获
 */
public class CrashModel {
    /*
        ************* Crash Log Head ****************
        Device Manufacturer: Meizu
        Device Model       : m3 note
        Android Version    : 5.1
        Android SDK        : 22
        App VersionName    : null
        App VersionCode    : 0
        ************* Crash Log Head ****************
        */
    private String DeviceManufacturer;
    private String DeviceModel;
    private String AndroidVersion;
    private int AndroidSDK;
    private String AppVersionName;
    private int AppVersionCode;
    private String Message;
    private String DateNow;

    public String getDateNow() {
        return DateNow;
    }

    public void setDateNow(String dateNow) {
        DateNow = dateNow;
    }

    public String getDeviceManufacturer() {
        return DeviceManufacturer;
    }

    public void setDeviceManufacturer(String deviceManufacturer) {
        DeviceManufacturer = deviceManufacturer;
    }

    public String getDeviceModel() {
        return DeviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        DeviceModel = deviceModel;
    }

    public String getAndroidVersion() {
        return AndroidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        AndroidVersion = androidVersion;
    }

    public int getAndroidSDK() {
        return AndroidSDK;
    }

    public void setAndroidSDK(int androidSDK) {
        AndroidSDK = androidSDK;
    }

    public String getAppVersionName() {
        return AppVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        AppVersionName = appVersionName;
    }

    public int getAppVersionCode() {
        return AppVersionCode;
    }

    public void setAppVersionCode(int appVersionCode) {
        AppVersionCode = appVersionCode;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
