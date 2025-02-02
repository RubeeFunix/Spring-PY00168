package poly.petshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import poly.petshop.domain.User;
import poly.petshop.repository.UserRepository;

@Service
public class OAuth2UserService {

    private final UserRepository userRepository;

    public OAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User processOAuth2User(OAuth2User oAuth2User) {
        String googleId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Lấy danh sách user có email trùng
        List<User> users = userRepository.findByEmail(email);
        User user;

        if (!users.isEmpty()) { // Nếu danh sách không rỗng, lấy user đầu tiên
            user = users.get(0);
        } else {
            user = new User();
            user.setEmail(email);
            user.setHoVaTen(name);
            user.setUserRole("User");
            userRepository.save(user);
        }
        return user;
    }

}
