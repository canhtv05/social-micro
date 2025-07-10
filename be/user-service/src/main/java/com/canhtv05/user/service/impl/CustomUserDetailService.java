package com.canhtv05.user.service.impl;

import com.canhtv05.user.enity.User;
import com.canhtv05.user.exception.AppException;
import com.canhtv05.user.exception.ErrorCode;
import com.canhtv05.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomUserDetailService implements UserDetailsService {

    UserRepository userRepository;

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_EMAIL_OR_PASSWORD));
    }
}
