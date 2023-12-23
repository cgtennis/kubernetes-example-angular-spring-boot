package tech.getarrays.employeemanager.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import tech.getarrays.employeemanager.service.EmployeeService;
import tech.getarrays.employeemanager.model.Employee;
import tech.getarrays.employeemanager.model.MyInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MyController {

    private final EmployeeService employeeService;

    public MyController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/info")
    public MyInfo getInfo() throws UnknownHostException {
        // Get local IP address
        String ipAddress = InetAddress.getLocalHost().getHostAddress();

        // get list of employees
        List<Employee> employees = employeeService.findAllEmployees();

        // return local ip address along with list of employees
        return new MyInfo(ipAddress, employees);
    }

}
