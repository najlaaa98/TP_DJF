package fr.ubo.hello.dao;

import fr.ubo.hello.bean.OTPRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OTPDaoMock implements OTPDao {
    private List<OTPRequest> otpRequests = new ArrayList<>();
    private AtomicInteger idCounter = new AtomicInteger(1);

    @Override
    public boolean saveOTPRequest(OTPRequest otpRequest) {
        otpRequest.setId(idCounter.getAndIncrement());
        otpRequests.add(otpRequest);
        System.out.println("OTP Mock sauvegardé - ID: " + otpRequest.getId() + ", Phone: " + otpRequest.getPhoneNumber());
        return true;
    }

    @Override
    public OTPRequest findValidOTP(String phoneNumber, LocalDateTime now) {
        return otpRequests.stream()
                .filter(otp -> otp.getPhoneNumber().equals(phoneNumber))
                .filter(otp -> !otp.isUsed())
                .filter(otp -> otp.getExpiresAt().isAfter(now))
                .filter(otp -> otp.getAttempts() < 3)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean markOTPAsUsed(int otpId) {
        return otpRequests.stream()
                .filter(otp -> otp.getId() == otpId)
                .findFirst()
                .map(otp -> {
                    otp.setUsed(true);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean incrementOTPAttempts(int otpId) {
        return otpRequests.stream()
                .filter(otp -> otp.getId() == otpId)
                .findFirst()
                .map(otp -> {
                    otp.setAttempts(otp.getAttempts() + 1);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public int cleanupExpiredOTPs(LocalDateTime now) {
        int initialSize = otpRequests.size();
        otpRequests.removeIf(otp -> otp.getExpiresAt().isBefore(now) || otp.getAttempts() >= 3);
        return initialSize - otpRequests.size();
    }

    @Override
    public int getRemainingAttempts(int otpId) {
        return otpRequests.stream()
                .filter(otp -> otp.getId() == otpId)
                .findFirst()
                .map(otp -> 3 - otp.getAttempts())
                .orElse(0);
    }

    @Override
    public boolean isOTPBlocked(int otpId) {
        return otpRequests.stream()
                .filter(otp -> otp.getId() == otpId)
                .findFirst()
                .map(otp -> otp.getAttempts() >= 3)
                .orElse(false);
    }

    @Override
    public int getTotalOTPRequests() {
        return otpRequests.size();
    }

    @Override
    public int getActiveOTPRequests(LocalDateTime now) {
        return (int) otpRequests.stream()
                .filter(otp -> !otp.isUsed())
                .filter(otp -> otp.getExpiresAt().isAfter(now))
                .filter(otp -> otp.getAttempts() < 3)
                .count();
    }

    @Override
    public int getUsedOTPRequests() {
        return (int) otpRequests.stream()
                .filter(OTPRequest::isUsed)
                .count();
    }
}