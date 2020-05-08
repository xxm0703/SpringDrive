package org.elsys.fileshare;

import org.elsys.fileshare.db.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class NodeController {
    @Autowired
    UserRepo users;

    @Autowired
    NodeRepo nodes;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> login(@RequestBody LoginContainer candidate) {


        Map<String, Object> response = new HashMap<>();
        response.put("token", entry.uuid);
        response.put("user", entry);
        response.put("node", new NodeInfo(entry));

        return response;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody LoginContainer candidate) {
        if (candidate == null) throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        NodeEntity root = new NodeEntity("root", null);
        nodes.save(root);
        final UserEntity userEntity = new UserEntity(candidate.username, candidate.password, root);
        users.save(userEntity);

    }
}
