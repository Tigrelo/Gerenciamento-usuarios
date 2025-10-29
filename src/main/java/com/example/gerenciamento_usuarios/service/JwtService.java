package com.example.gerenciamento_usuarios.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // 1. Pega o segredo do application.properties
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    // 2. Define o tempo de expiração do token (aqui, 24 horas)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // --- Métodos Públicos Principais ---

    /**
     * Extrai o e-mail (que é o "subject" ou "dono" do token).
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Gera um novo token JWT para um usuário.
     */
    public String generateToken(UserDetails userDetails) {
        // O "subject" do token será o username (nosso e-mail)
        return buildToken(new HashMap<>(), userDetails.getUsername(), EXPIRATION_TIME);
    }

    /**
     * Verifica se o token é válido:
     * 1. O e-mail dentro do token é o mesmo do usuário?
     * 2. O token já expirou?
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // --- Métodos Auxiliares ---

    private boolean isTokenExpired(String token) {
        // Verifica se a data de expiração do token é *antes* da data de agora
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Método genérico para extrair qualquer "claim" (informação) de dentro do token.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * O "coração" da leitura: decodifica o token usando a chave secreta.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey()) // Verifica a assinatura
                .build()
                .parseSignedClaims(token) // Decodifica
                .getPayload(); // Retorna a "carga" (os dados)
    }

    /**
     * O "coração" da criação: constrói o token.
     */
    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject) // O "dono" do token
                .issuedAt(new Date(System.currentTimeMillis())) // Data de criação
                .expiration(new Date(System.currentTimeMillis() + expiration)) // Data de expiração
                .signWith(getSignInKey(), Jwts.SIG.HS256) // Assina com nossa chave
                .compact();
    }

    /**
     * Converte nossa String secreta (Base64) em um objeto SecretKey que o JJWT entende.
     */
    private SecretKey getSignInKey() {
        // Decodifica a chave que está em Base64
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        // Transforma os bytes em uma chave criptográfica HMAC-SHA
        return Keys.hmacShaKeyFor(keyBytes);
    }
}