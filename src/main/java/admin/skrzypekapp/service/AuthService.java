package admin.skrzypekapp.service;

import admin.skrzypekapp.dto.RegisterRequest;
import admin.skrzypekapp.entity.User;
import admin.skrzypekapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Użytkownik już istnieje!");
        }
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email jest zajęty");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole("ROLE_ADMIN");
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }
}