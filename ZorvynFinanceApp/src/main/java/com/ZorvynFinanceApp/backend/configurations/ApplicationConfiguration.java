package com.ZorvynFinanceApp.backend.configurations;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.KeyGenerator;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ZorvynFinanceApp.backend.models.RoleType;
import com.ZorvynFinanceApp.backend.models.User;
import com.ZorvynFinanceApp.backend.repositories.UserRepo;

import tools.jackson.databind.ObjectMapper;

@Configuration
public class ApplicationConfiguration {

    
    @Bean
    ModelMapper modelMapper(){
        return new ModelMapper();
    }
 
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
	public Key key() throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
		keyGenerator.init(256);
		return keyGenerator.generateKey();
	}
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRepo userRepo, PasswordEncoder passwordEncoder){
        return args -> {
            try {
                
                //creating a admin user
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("pass"));
                Set<RoleType> adminRoles = new HashSet<>();
                adminRoles.add(RoleType.ADMIN);
                adminUser.setRoles(adminRoles);
                userRepo.save(adminUser);
                System.out.println("created default admin user: username: admin password: pass");

                //creating a viewer user 
                User viewerUser = new User();
                viewerUser.setUsername("viewer");
                viewerUser.setPassword(passwordEncoder.encode("pass"));
                Set<RoleType> viewerRoles = new HashSet<>();
                viewerRoles.add(RoleType.VIEWER);
                viewerUser.setRoles(viewerRoles);
                userRepo.save(viewerUser);
                System.out.println("created default viewer user: username: viewer password: pass");

                //creating a analyst user
                User analystUser = new User();
                analystUser.setUsername("analyst");
                analystUser.setPassword(passwordEncoder.encode("pass"));
                Set<RoleType> analystRoles = new HashSet<>();
                analystRoles.add(RoleType.ANALYST);
                analystUser.setRoles(analystRoles);
                userRepo.save(analystUser);
                System.out.println("created default analyst user: username: analyst password: pass");
                
            } catch (DataIntegrityViolationException e) {
                System.out.println("default users already exist");
                System.out.println("admin user -> username: admin password: pass");
                System.out.println("admin viewer -> username: viewer password: pass");
                System.out.println("admin analyst -> username: analyst password: pass");
            }
        };
    }
}