package org.elsys.fileshare;

import org.elsys.fileshare.db.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/nodes")
public class NodeController {
    @Autowired
    UserRepo users;

    @Autowired
    NodeRepo nodes;

    @Autowired
    ContentRepo contents;

    @GetMapping("/fetch/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> fetch(@RequestParam String token, @PathVariable int id) {
        final UserEntity user = this.users.findByUuid(token);
        final NodeEntity node = this.nodes.findById(id);

        if (user == null || !this.validateOwnership(user.id, node))
            return ResponseEntity.badRequest().body(null);


        return ResponseEntity.ok(getResponse(user, node));
    }

    @PostMapping("/create/{name}/{isFile}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createNode(@RequestBody CredentialsContainer data,
                                        @PathVariable String name,
                                        @RequestParam int parent_id,
                                        @PathVariable boolean isFile) {
        final UserEntity user = this.users.findByUuid(data.token);

        ContentEntity contentEntity = null;
        if (isFile) {
            contentEntity = new ContentEntity("");
            contents.save(contentEntity);
        }
        NodeEntity parent = nodes.findById(parent_id);

        if (!this.validateOwnership(user.id, parent))
            return ResponseEntity.badRequest().body(null);

        final NodeEntity node = new NodeEntity(name, parent, contentEntity);
        nodes.save(node);

        return ResponseEntity.accepted().body(getResponse(user, node));
    }

    @NotNull
    private Map<String, Object> getResponse(UserEntity user, NodeEntity node) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", user.uuid);
        response.put("user", user);
        response.put("node", new NodeInfo(node));
        return response;
    }

    private boolean validateOwnership(int userId, NodeEntity node) {
        while (node.parent != null)
            node = node.parent;
        return node.owner.id == userId;
    }

}
