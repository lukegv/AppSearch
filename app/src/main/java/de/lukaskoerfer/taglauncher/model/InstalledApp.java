package de.lukaskoerfer.taglauncher.model;

/**
 * Created by Koerfer on 07.04.2016.
 */
public class InstalledApp {

    private String packageName;
    private String appName;

    public InstalledApp(String packageName, String appName) {
        this.packageName = packageName;
        this.appName = appName;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getAppName() {
        return this.appName;
    }

}
