package org.elsys.fileshare;

import org.elsys.fileshare.db.*;
import org.elsys.fileshare.emails.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private EmailSenderService emailSenderService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> login(HttpServletRequest request,
                                   @RequestBody CredentialsContainer candidate) {
        UserEntity entry = users.findByUsername(candidate.username);

        if (entry == null || !entry.correctPass(candidate.password) || !entry.enabled) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        entry.uuid = UUID.randomUUID().toString();
        users.save(entry);

        Map<String, Object> response = new HashMap<>();
        response.put("token", entry.uuid);
        response.put("user", entry);
        response.put("node", new NodeInfo(entry));
        response.put("host", request.getServerName() + ':' + request.getServerPort());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(HttpServletRequest request, @RequestBody CredentialsContainer candidate) {
        if (candidate == null || users.findByUsername(candidate.username) != null)
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        NodeEntity root = new NodeEntity("root", null);
        nodes.save(root);
        final UserEntity userEntity = new UserEntity(candidate, root);
        users.save(userEntity);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userEntity.email);
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("chand312902@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                + String.format("http://localhost:%d/api/users/confirm-account?token=%s", request.getServerPort(), userEntity.uuid));

        emailSenderService.sendEmail(mailMessage);

        return ResponseEntity.created(null).body(null);
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken) {
        UserEntity userEntity = users.findByUuid(confirmationToken);

        if (userEntity != null) {
            userEntity.enabled = true;
            users.save(userEntity);
            return ResponseEntity.ok().body(null);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
