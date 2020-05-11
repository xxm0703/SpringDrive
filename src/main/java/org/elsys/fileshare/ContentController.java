package org.elsys.fileshare;

import org.elsys.fileshare.db.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/contents")
public class ContentController {
    @Autowired
    UserRepo users;

    @Autowired
    NodeRepo nodes;

    @Autowired
    ContentRepo contents;


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> renameNode(@RequestBody ContentEntity data,
                                        @RequestParam String token,
                                        @PathVariable int id) {


        if (!this.validateOwnership(token, id))
            return ResponseEntity.badRequest().body(null);

        final ContentEntity entity = this.contents.findById(id);
        final UserEntity user = this.users.findByUuid(token);
        entity.text = data.text;
        this.contents.save(entity);

        return ResponseEntity.ok(getResponse(user, entity.file));
    }


    @NotNull
    private Map<String, Object> getResponse(UserEntity user, NodeEntity node) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", user.uuid);
        response.put("user", user);
        response.put("node", new NodeInfo(node));
        return response;
    }

    private boolean validateOwnership(String userToken, int contentId) {
        final UserEntity user = this.users.findByUuid(userToken);
        NodeEntity node = this.contents.findById(contentId).file;

        if (user == null || node == null)
            return false;

        while (node.parent != null)
            node = node.parent;
        return node.owner.id == user.id;
    }

}
