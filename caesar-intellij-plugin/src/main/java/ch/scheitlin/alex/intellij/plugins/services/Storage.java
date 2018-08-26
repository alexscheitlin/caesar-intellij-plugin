package ch.scheitlin.alex.intellij.plugins.services;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.ServiceManager;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Storage {
    private final String HOST = "BUILD_SERVER_HOST";
    private final String USERNAME = "BUILD_SERVER_USERNAME";
    private final String PASSWORD = "BUILD_SERVER_PASSWORD";

    public static Storage getInstance() {
        return ServiceManager.getService(Storage.class);
    }

    public void saveBuildServerCredentials(String host, String username, String password) {
        PropertiesComponent properties = PropertiesComponent.getInstance();
        properties.setValue(HOST, host);
        properties.setValue(USERNAME, username);
        properties.setValue(PASSWORD, password);
    }

    public List<Pair<String, String>> getBuildServerCredentials() {
        PropertiesComponent properties = PropertiesComponent.getInstance();
        List<Pair<String, String>> credentials = new ArrayList<>();
        credentials.add(new Pair<>(HOST, properties.getValue(HOST)));
        credentials.add(new Pair<>(USERNAME, properties.getValue(USERNAME)));
        credentials.add(new Pair<>(PASSWORD, properties.getValue(PASSWORD)));

        return credentials;
    }

    public void deleteBuildServerCredentials() {
        PropertiesComponent properties = PropertiesComponent.getInstance();
        properties.unsetValue(HOST);
        properties.unsetValue(USERNAME);
        properties.unsetValue(PASSWORD);
    }
}
