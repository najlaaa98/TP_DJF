package fr.ubo.hello.bean;

public class AuthRequest {
    private String phoneNumber;
    private String otpCode;
    private String email;
    private String password;

    public AuthRequest() {}

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AuthRequest(String phoneNumber, String otpCode, String type) {
        this.phoneNumber = phoneNumber;
        this.otpCode = otpCode;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}