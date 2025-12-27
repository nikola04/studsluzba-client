package org.raflab.studsluzbadesktopclient.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.LinkedList;

@Service
public class NavigationHistoryService {

    @Value("${app.navigation.history.max:10}")
    private int maxDepth;

    private final LinkedList<String> backStack = new LinkedList<>();
    private final LinkedList<String> forwardStack = new LinkedList<>();
    private String currentFxml;

    public void push(String fxmlPath) {
        if (fxmlPath.equals(currentFxml)) return;

        if (currentFxml != null) {
            backStack.push(currentFxml);
            if (backStack.size() > maxDepth) {
                backStack.removeLast();
            }
        }
        currentFxml = fxmlPath;
        forwardStack.clear();
    }

    public String popBack() {
        if (backStack.isEmpty()) return null;

        forwardStack.push(currentFxml);
        currentFxml = backStack.pop();
        return currentFxml;
    }

    public String popForward() {
        if (forwardStack.isEmpty()) return null;

        backStack.push(currentFxml);
        currentFxml = forwardStack.pop();
        return currentFxml;
    }

}