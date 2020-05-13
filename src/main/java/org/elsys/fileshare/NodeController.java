package org.elsys.fileshare;

import org.elsys.fileshare.db.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/nodes")
public class NodeController {
    @Autowired
    UserRepo users;

    @Autowired
    LinkRepo links;

    @Autowired
    NodeRepo nodes;

    @Autowired
    ContentRepo contents;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> fetch(HttpServletRequest request,
                                   @RequestParam String token,
                                   @PathVariable int id,
                                   @RequestParam(required = false) String link) {

        if (!this.validateOwnership(token, id, link))
            return ResponseEntity.badRequest().body(null);

        final UserEntity user = this.users.findByUuid(token);
        final NodeEntity node = this.nodes.findById(id);

        return ResponseEntity.ok(getResponse(user, node, request.getServerName() + ':' + request.getServerPort()));
    }

    @GetMapping("/link/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> generateLink(@RequestParam String token,
                                          @PathVariable int id,
                                          @RequestParam(required = false) String link) {

        final NodeEntity node = this.nodes.findById(id);

        if (!this.validateOwnership(token, id, null) || node.link != null) {
            return ResponseEntity.badRequest().body(null);
        }

        final LinkEntity linkEntity = new LinkEntity(node);

        nodes.save(node);
        links.save(linkEntity);

        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/shared")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getInfo(@RequestParam String token,
                                          @RequestParam String link) {

        if (!users.existsByUuid(token))
            return ResponseEntity.badRequest().body(null);

        final LinkEntity linkEntity = links.findByToken(link);

        Map<String, Object> response = new HashMap<>();
        response.put("file", linkEntity.node.content != null);
        response.put("nodeId", linkEntity.node.id);
        response.put("name", linkEntity.node.name);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{id}")
    public HttpEntity<byte[]> downloadBin(@RequestParam String token,
                                          @PathVariable int id,
                                          @RequestParam(required = false) String link) throws IOException {

        if (!this.validateOwnership(token, id, link))
            return ResponseEntity.badRequest().body(null);

        NodeEntity entity = nodes.findById(id);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + entity.name);
        header.setContentLength(entity.content.text.length);

        return new HttpEntity<>(entity.content.text, header);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteNode(@RequestParam String token,
                                        @PathVariable int id) {

        if (!this.validateOwnership(token, id, null))
            return ResponseEntity.badRequest().body(null);

        this.nodes.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> renameNode(@RequestParam String token,
                                        @PathVariable int id,
                                        @RequestParam(name = "name") String newName) {

        if (!this.validateOwnership(token, id, null))
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

        if (!this.validateOwnership(data.token, parent_id, null)) {
            return ResponseEntity.badRequest().body(null);
        }

        final UserEntity user = this.users.findByUuid(data.token);
        final NodeEntity parent = nodes.findById(parent_id);

        final NodeEntity node = new NodeEntity(name, parent, contentEntity);
        nodes.save(node);

        return ResponseEntity.accepted().body(null);
    }

    private Map<String, Object> getResponse(UserEntity user, NodeEntity node, String addr) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", user.uuid);
        response.put("user", user);
        response.put("node", new NodeInfo(node));
        response.put("host", addr);
        return response;
    }

    private boolean validateOwnership(String userToken, int nodeId, String link) {
        final UserEntity user = this.users.findByUuid(userToken);
        final LinkEntity linkEntity = this.links.findByToken(link);
        NodeEntity node = this.nodes.findById(nodeId);

        if (user == null || node == null)
            return false;

        while (node.parent != null) {
            if (linkEntity != null && linkEntity.node.id.equals(node.id))
                return true;
            node = node.parent;
        }
        return node.owner.id == user.id;
    }

}
