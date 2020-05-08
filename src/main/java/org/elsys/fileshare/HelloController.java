package org.elsys.fileshare;

import org.elsys.fileshare.db.NodeEntity;
import org.elsys.fileshare.db.NodeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloController {
    @Autowired
    private NodeRepo repo;

    @GetMapping("/hello")
    public String hello() {
        NodeEntity file = repo.findAll().get(0);
//        repo.save(new NodeEntity(""));
//        return "Hello, the time at the server is now " + new Date() + "\n";
        return file.toString();
    }

}