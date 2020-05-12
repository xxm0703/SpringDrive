package org.elsys.fileshare;

import org.elsys.fileshare.db.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class AccountController {
    @Autowired
    UserRepo users;

    @Autowired
    NodeRepo nodes;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> login(@RequestBody CredentialsContainer candidate) {
        UserEntity entry = users.findByUsername(candidate.username);

        if (entry == null || !entry.correctPass(candidate.password)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        entry.uuid = UUID.randomUUID().toString();
        users.save(entry);

        Map<String, Object> response = new HashMap<>();
        response.put("token", entry.uuid);
        response.put("user", entry);
        response.put("node", new NodeInfo(entry));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@RequestBody CredentialsContainer candidate) {
        if (candidate == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        NodeEntity root = new NodeEntity("root", null);
        nodes.save(root);
        final UserEntity userEntity = new UserEntity(candidate, root);
        users.save(userEntity);
        return login(candidate);
    }
}
