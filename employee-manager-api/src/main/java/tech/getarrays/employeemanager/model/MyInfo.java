package tech.getarrays.employeemanager.model;

import java.util.List;
public class MyInfo {

    private final String ipAddress;
    private final List<Employee> employees;

    public MyInfo(String ipAddress, List<Employee> employees) {
        this.ipAddress = ipAddress;
        this.employees = employees;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public List<Employee> getEmployees() {
        return employees;
    }
}
