package controller.web.dto;

public record AuthResponse(String accessToken, String userId, String name, String role) {}
