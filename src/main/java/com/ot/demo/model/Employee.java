package com.ot.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class Employee {
    @JsonProperty("employee_id")
    private String employeeId;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("dept")
    private String dept;

    @JsonProperty("manager")
    private String manager;
}
