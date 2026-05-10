package com.example.homely.data.model;

// Model ánh xạ kết quả từ API tỉnh thành Việt Nam
// API: https://provinces.open-api.vn/api/?depth=1

public class Province {
    private int code;
    private String name;
    private String codename;
    private String division_type;
    private String phone_code;

    public Province() {}

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCodename() { return codename; }
    public void setCodename(String codename) { this.codename = codename; }

    public String getDivision_type() { return division_type; }
    public void setDivision_type(String division_type) { this.division_type = division_type; }

    public String getPhone_code() { return phone_code; }
    public void setPhone_code(String phone_code) { this.phone_code = phone_code; }

    // Dùng cho ArrayAdapter hiển thị trong dropdown
    @Override
    public String toString() { return name; }
}
