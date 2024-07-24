package com.az.gretapyta.questionnaires.security;

import java.io.Serializable;

public record JwtResponse(String jwttoken) implements Serializable {
}