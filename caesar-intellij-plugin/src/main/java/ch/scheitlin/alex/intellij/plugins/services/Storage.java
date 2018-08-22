package ch.scheitlin.alex.intellij.plugins.services;

import ch.scheitlin.alex.intellij.plugins.toolWindow.ToolWindow;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Storage {
    public Project project;
    public ToolWindow toolWindow;
    public String teamCityProjectName;
    public String teamCityBuildConfigurationName;
    public Object selectedTeamCityProject;

    public final String HOST = "TEAM_CITY_HOST";
    public final String USERNAME = "TEAM_CITY_USERNAME";
    public final String PASSWORD = "TEAM_CITY_PASSWORD";

    public static Storage getInstance() {
        return ServiceManager.getService(Storage.class);
    }

    public void saveTeamCityCredentials(String host, String username, String password) {
        PropertiesComponent properties = PropertiesComponent.getInstance();
        properties.setValue(HOST, host);
        properties.setValue(USERNAME, username);
        properties.setValue(PASSWORD, password);
    }

    public List<Pair<String, String>> getTeamCityCredentials() {
        PropertiesComponent properties = PropertiesComponent.getInstance();
        List<Pair<String, String>> teamCityCredentials = new ArrayList<>();
        teamCityCredentials.add(new Pair<>(HOST, properties.getValue(HOST)));
        teamCityCredentials.add(new Pair<>(USERNAME, properties.getValue(USERNAME)));
        teamCityCredentials.add(new Pair<>(PASSWORD, properties.getValue(PASSWORD)));

        return teamCityCredentials;
    }

    public void deleteTeamCityCredentials() {
        PropertiesComponent properties = PropertiesComponent.getInstance();
        properties.unsetValue(HOST);
        properties.unsetValue(USERNAME);
        properties.unsetValue(PASSWORD);
    }
}
