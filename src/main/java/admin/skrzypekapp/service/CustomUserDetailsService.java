package admin.skrzypekapp.service;

import admin.skrzypekapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(u -> User.builder()
                        .username(u.getUsername())
                        .password(u.getPassword())
                        .roles(u.getRole().replace("ROLE_", "")) // Spring sam doda prefiks ROLE_
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}