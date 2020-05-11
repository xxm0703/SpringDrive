package org.elsys.fileshare;

import org.elsys.fileshare.db.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Blob;
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

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> fetch(@RequestParam String token,
                                   @PathVariable int id) {

        if (!this.validateOwnership(token, id))
            return ResponseEntity.badRequest().body(null);

        final UserEntity user = this.users.findByUuid(token);
        final NodeEntity node = this.nodes.findById(id);

        return ResponseEntity.ok(getResponse(user, node));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteNode(@RequestParam String token,
                                        @PathVariable int id) {

        if (!this.validateOwnership(token, id))
            return ResponseEntity.badRequest().body(null);

        this.nodes.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> renameNode(@RequestParam String token,
                                        @PathVariable int id,
                                        @RequestParam(name = "name") String newName) {

        if (!this.validateOwnership(token, id))
            return ResponseEntity.badRequest().body(null);

        final NodeEntity node = this.nodes.findById(id);
        node.name = newName;
        nodes.save(node);

        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/{name}/{isFile}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createNode(@RequestBody CredentialsContainer data,
                                        @PathVariable String name,
                                        @RequestParam int parent_id,
                                        @PathVariable boolean isFile) {

        ContentEntity contentEntity = null;
        if (isFile) {
            contentEntity = new ContentEntity(null);
            contents.save(contentEntity);
        }

        if (!this.validateOwnership(data.token, parent_id)) {
            return ResponseEntity.badRequest().body(null);
        }

        final UserEntity user = this.users.findByUuid(data.token);
        final NodeEntity parent = nodes.findById(parent_id);

        final NodeEntity node = new NodeEntity(name, parent, contentEntity);
        nodes.save(node);

        return ResponseEntity.accepted().body(null);
    }

    @NotNull
    private Map<String, Object> getResponse(UserEntity user, NodeEntity node) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", user.uuid);
        response.put("user", user);
        response.put("node", new NodeInfo(node));
        return response;
    }

    private boolean validateOwnership(String userToken, int nodeId) {
        final UserEntity user = this.users.findByUuid(userToken);
        NodeEntity node = this.nodes.findById(nodeId);

        if (user == null || node == null)
            return false;

        while (node.parent != null)
            node = node.parent;
        return node.owner.id == user.id;
    }

}
