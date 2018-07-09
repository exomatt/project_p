package com.dreamteam.project.config;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ConfigReader {

    public static Map<String, List<String>> loadPermissions(String className) {
        Map<String, List<String>> permissions = new HashMap<>();
        String csvFile = className.replace("Commands", "Permission.csv");
        String csvSplitBy = ",";
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                List<String> roles = new ArrayList<>();
                String[] permission = line.split(csvSplitBy);

                for (int i = 1; i < permission.length; i++) {
                    roles.add(permission[i]);
                }
                permissions.put(permission[0], roles);
            }
            if(permissions.isEmpty()){
                log.error("Document permissions are not ready to use");
            }
            return permissions;
        } catch (IOException e) {
            log.error("File not found", e);
            return permissions;
        }
    }
}
