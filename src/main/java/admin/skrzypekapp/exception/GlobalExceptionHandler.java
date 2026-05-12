package admin.skrzypekapp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Nieprawidłowy format danych");
        response.put("message", "Oczekiwano liczby, a otrzymano: " + ex.getValue());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Nieudana próba logowania. Złe dane uwierzytelniające.");

        Map<String, String> response = new HashMap<>();
        response.put("error", "Błąd autoryzacji");
        response.put("message", "Nieprawidłowy login lub hasło.");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {

        log.error("Krytyczny błąd serwera: ", ex);

        Map<String, String> response = new HashMap<>();
        response.put("error", "Wewnętrzny błąd serwera");
        response.put("message", "Coś poszło nie tak. Spróbuj ponownie później.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}