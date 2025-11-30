package fr.ubo.hello.bean;

import java.time.LocalDateTime;

public class OTPRequest {
    private int id;
    private String phoneNumber;
    private String otpCode;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean used;
    private int attempts;

    public OTPRequest() {}

    public OTPRequest(String phoneNumber, String otpCode, LocalDateTime expiresAt) {
        this.phoneNumber = phoneNumber;
        this.otpCode = otpCode;
        this.expiresAt = expiresAt;
        this.used = false;
        this.attempts = 0;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
}