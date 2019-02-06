package io.suffragium.CommunityService.controller;

import io.suffragium.CommunityService.community.CommunityRepository;
import io.suffragium.common.entity.community.Community;
import io.suffragium.common.entity.community.CommunityMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/hal+json")
public class CommunityRestController {
    private  final CommunityResourceAssembler communityResourceAssembler;
    private CommunityRepository communityRepository;

    @Autowired
    CommunityRestController(CommunityResourceAssembler communityResourceAssembler, CommunityRepository communityRepository) {
        this.communityResourceAssembler = communityResourceAssembler;
        this.communityRepository = communityRepository;
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    ResponseEntity<?> options() {
        return ResponseEntity.ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.HEAD,
                        HttpMethod.OPTIONS, HttpMethod.PUT, HttpMethod.DELETE)
                .build();
    }

    @GetMapping
    ResponseEntity<Resources<Object>> root() {
        Resources<Object> objects = new Resources<>(Collections.emptyList());
        URI uri = MvcUriComponentsBuilder
                .fromMethodCall(MvcUriComponentsBuilder.on(getClass()).getCollection())
                .build().toUri();
        Link link = new Link(uri.toString(), "communities");
        objects.add(link);
        return ResponseEntity.ok(objects);
    }

    @GetMapping("/communities")
    ResponseEntity<Resources<Resource<Community>>> getCollection() {
        List<Resource<Community>> collect = this.communityRepository.findAll().stream()
                .map(communityResourceAssembler::toResource).collect(Collectors.<Resource<Community>>toList());
        Resources<Resource<Community>> resources = new Resources<>(collect);
        URI self = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        resources.add(new Link(self.toString(), "self"));
        return ResponseEntity.ok(resources);
    }

    @GetMapping(value = "/communities/{id}")
    ResponseEntity<Resource<Community>> getCommunityResource(@PathVariable Long id) {
        return this.communityRepository.findById(id)
                .map(c -> ResponseEntity.ok(this.communityResourceAssembler.toResource(c)))
                .orElseThrow(() -> new CommunityNotFoundException(id));
    }

    @GetMapping(value = "/communities/{id}/object")
    ResponseEntity<Community> get(@PathVariable Long id) {
        return this.communityRepository.findById(id)
                .map(c -> ResponseEntity.ok(c))
                .orElseThrow(() -> new CommunityNotFoundException(id));
    }

    @PostMapping(value = "/communities")
    ResponseEntity<Resource<Community>> post(@RequestBody Community c) {
        Community community = this.communityRepository.save(new Community(c));
        URI uri = MvcUriComponentsBuilder.fromController(getClass())
                .path("/communities/{id}").buildAndExpand(community.getId()).toUri();
        return ResponseEntity.created(uri).body(
                this.communityResourceAssembler.toResource(community));
    }

    @PostMapping(value = "/communities/{id}/add-member")
    ResponseEntity<Resource<Community>> addMemberToCommunity(@PathVariable Long id, @RequestBody Set<Long> members) {
        return this.communityRepository.findById(id)
                .map(c -> {
                    this.communityRepository.save(c.addMembers(members));
                    Resource<Community> communityResource = this.communityResourceAssembler.toResource(c);
                    URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
                    return ResponseEntity.created(selfLink).body(communityResource);
                })
                .orElseThrow(() -> new CommunityNotFoundException(id));
    }

    @DeleteMapping(value = "/communities/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        return this.communityRepository.findById(id).map(c -> {
            communityRepository.delete(c);
            return ResponseEntity.noContent().build();
        }).orElseThrow(() -> new CommunityNotFoundException(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    ResponseEntity<?> head(@PathVariable Long id) {
        return this.communityRepository.findById(id)
                .map(exist -> ResponseEntity.noContent().build())
                .orElseThrow(() -> new CommunityNotFoundException(id));
    }

    @PutMapping("/communities/{id}")
    ResponseEntity<Resource<Community>> put(@PathVariable Long id,
                                           @RequestBody Community c) {
        Community community = this.communityRepository.save(new Community(c));
        Resource<Community> communityResource = this.communityResourceAssembler.toResource(community);
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(communityResource);
    }
}
