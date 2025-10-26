package controller;

import view.LoginView;
import view.DashboardView;

public class LoginController {
    private LoginView view;
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "1234";

    public LoginController(LoginView view) {
        this.view = view;
    }

    public void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (isValidCredentials(username, password)) {
            view.showSuccessMessage();
            openDashboard();
            view.closeWindow();
        } else {
            view.showErrorMessage();
        }
    }

    private boolean isValidCredentials(String username, String password) {
        return VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password);
    }

    private void openDashboard() {
        DashboardView dashboard = new DashboardView();
        dashboard.showWindow();
    }
}
