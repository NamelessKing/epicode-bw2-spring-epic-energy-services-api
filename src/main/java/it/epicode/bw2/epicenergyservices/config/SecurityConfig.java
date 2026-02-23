package it.epicode.bw2.epicenergyservices.config;

import it.epicode.bw2.epicenergyservices.security.JWTCheckerFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 1. Configurare la catena di filter (JWT Filter)
 * 2. Definire endpoint pubblici vs protetti
 * 3. Configurare BCrypt per l'hashing delle password
 * 4. Abilitare CORS per il frontend
 * 5. Disabilitare sessioni (stateless API)
 * 6. Abilitare @PreAuthorize sui metodi controller
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Abilita @PreAuthorize sui metodi
public class SecurityConfig {
    
    /**
     * Bean PasswordEncoder - BCrypt per hashing password
     * Strength 12 = 2^12 iterazioni (più sicuro, slightly più lento)
     * 
     * Usato in UsersService.save() per hashare password durante registrazione
     * Usato in AuthService per verificare password durante login
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    /**
     * Configurazione CORS - permette richieste da frontend
     * 
     * Permette:
     * - Qualsiasi origine (origin)
     * - Tutti i metodi HTTP (GET, POST, PUT, DELETE, etc.)
     * - Tutti gli header
     * 
     *In PRODUCTION: Restringere a domini specifici
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * Catena di sicurezza principale
     * 
     * Configura:
     * 1. CORS abilitato
     * 2. CSRF disabilitato (non serve per JWT stateless API)
     * 3. Sessioni stateless (niente HttpSession)
     * 4. Autorizzazione: endpoint pubblici vs protetti
     * 5. JWT Filter aggiunto prima dell'UsernamePasswordAuthenticationFilter
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, 
                                          JWTCheckerFilter jwtCheckerFilter) 
            throws Exception {
        
        return http
            // ==================== CORS ====================
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // ==================== CSRF ====================
            // Disabilita CSRF (non necessario per JWT stateless API)
            .csrf(csrf -> csrf.disable())
            
            // ==================== SESSION MANAGEMENT ====================
            // Stateless: non memorizza sessioni nel server
            // Ogni richiesta deve contenere il token JWT
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // ==================== AUTHORIZATION RULES ====================
            .authorizeHttpRequests(auth -> auth
                // Endpoint pubblici (nessun token richiesto)
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                
                // Tutti gli altri endpoint richiedono autenticazione
                .anyRequest().authenticated()
            )
            
            // ==================== JWT FILTER ====================
            // Aggiungi JWT Filter PRIMA dell'UsernamePasswordAuthenticationFilter
            // In questo modo il JWT viene verificato per ogni richiesta protetta
            .addFilterBefore(jwtCheckerFilter, UsernamePasswordAuthenticationFilter.class)
            
            .build();
    }
}
