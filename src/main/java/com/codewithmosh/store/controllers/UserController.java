package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ChangePasswordRequest;
import com.codewithmosh.store.dtos.RegisterUserRequest;
import com.codewithmosh.store.dtos.UpdateUserRequest;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.entities.Role;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    //@RequestMapping("/users")
    //method:GET,
    @GetMapping
    public Iterable<UserDto> getAllUsers(
            // This request header is not  case sensitive.By default it is required, so if we don't provide it, we get a bad request 400 error meaning we did
            // provide the necessary data for calling our end point.So just like @RequestParam we can make it optional by setting required attribute to false.'
            //@RequestHeader(required= false,name = "x-auth-token") String  authToken,





            //Here we are capturing the name parameter from the request. So we are using String
            //It could be anything like Byte ,int, long etc. any kind of numeric types.Now if
            // the value that is provided cannot be convertible to the target type,we get a runtime exception.
            // By default the sort parameter is required. So if we don't provide it, we get a bad request 400 error'.
            /// we can make it optional by setting required  attribute of request param to false.

           @RequestParam (required = false,defaultValue = "", name="sort")String SortBy
    ){

       // System.out.println("authToken: " + authToken);

        // Now what if we provide an invalid field like namex , we get a runtime error that translates to a 500 error in http.
        // Not the best experience for our clients.Better approach is to validate sort parameter and if it is invalid, we can give it
        // a default value
        if(!Set.of("name","email").contains(SortBy))
            //we cannot pass a null to contains method. So we use default value attribute of requestParam
            SortBy ="name";

// findAll() has an overload that takes a sort object. if we press ctrl+p we can see all the overloads like Sort sort, Pageable pageable etc objects
        //and these are available in repositories that extend JPA repository and this is the interface that provides sorting and pagination.
        return userRepository.findAll(Sort.by(SortBy))
                .stream()
                //.map(user->new UserDto(user.getId(),user.getName(),user.getEmail()))
               // .map(user -> userMapper.toDto(user))
                .map(userMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id ){
        var user=  userRepository.findById(id).orElse(null);
        if (user ==  null){
            // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return ResponseEntity.notFound().build();
        }
        //return new ResponseEntity<>(user,HttpStatus.OK);
        // we can use static factory methods
       // var userDto = new UserDto(user.getId(),user.getName(),user.getEmail());
        // return ResponseEntity.ok(userDto);
        return ResponseEntity.ok(userMapper.toDto(user));

    }
    @PostMapping
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(
                    Map.of("email", "Email is already registered.")
            );
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        var userDto = userMapper.toDto(user);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();

        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable(name = "id") Long id,
            @RequestBody UpdateUserRequest request) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userMapper.update(request, user);
        userRepository.save(user);

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (!user.getPassword().equals(request.getOldPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }
}
