package de.lukaskoerfer.appsearch.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Koerfer on 07.04.2016.
 */
public class InstalledApp {

    private String packageName;
    private String appName;

    public List<String> localTags;
    public List<String> globalTags;

    public InstalledApp(String packageName, String appName) {
        this.packageName = packageName;
        this.appName = appName;
        this.localTags = new ArrayList<>();
        this.globalTags = new ArrayList<>();
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getAppName() {
        return this.appName;
    }

    public int matchFactor(String constraint) {
        int factor = this.localTags.contains(constraint) ? 1 : 0;
        factor += StringUtils.containsIgnoreCase(this.appName, constraint) ? 1 : 0;
        return factor;
    }
}
