package fr.ubo.hello.dao;

import fr.ubo.hello.bean.OTPRequest;
import java.time.LocalDateTime;

public interface OTPDao {
    boolean saveOTPRequest(OTPRequest otpRequest);
    OTPRequest findValidOTP(String phoneNumber, LocalDateTime now);
    boolean markOTPAsUsed(int otpId);
    boolean incrementOTPAttempts(int otpId);
    int cleanupExpiredOTPs(LocalDateTime now);
    int getRemainingAttempts(int otpId);
    boolean isOTPBlocked(int otpId);
    int getTotalOTPRequests();
    int getActiveOTPRequests(LocalDateTime now);
    int getUsedOTPRequests();
}