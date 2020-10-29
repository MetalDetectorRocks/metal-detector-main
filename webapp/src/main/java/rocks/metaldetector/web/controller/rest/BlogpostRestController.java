package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rocks.metaldetector.persistence.domain.blogpost.BlogpostEntity;
import rocks.metaldetector.service.blogpost.BlogpostService;
import rocks.metaldetector.web.api.request.BlogpostCreateRequest;

import javax.validation.Valid;
import java.util.List;

import static rocks.metaldetector.config.constants.Endpoints.Rest.BLOGPOSTS;

@RestController
@RequestMapping(BLOGPOSTS)
@AllArgsConstructor
public class BlogpostRestController {

    private final BlogpostService blogpostService;

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<BlogpostEntity>> getAllBlogposts() {
        var blogposts = blogpostService.getAllBlogposts();
        return ResponseEntity.ok(blogposts);
    }
}
