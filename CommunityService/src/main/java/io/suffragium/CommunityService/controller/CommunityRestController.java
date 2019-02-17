package io.suffragium.CommunityService.controller;

import io.suffragium.CommunityService.community.CommunityRepository;
import io.suffragium.CommunityService.service.CommunityService;
import io.suffragium.common.entity.community.Community;
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
@RequestMapping(produces = "application/hal+json")
public class CommunityRestController {
    private  final CommunityResourceAssembler communityResourceAssembler;
    private CommunityRepository communityRepository;
    private CommunityService communityService;

    @Autowired
    CommunityRestController(CommunityResourceAssembler communityResourceAssembler, CommunityRepository communityRepository, CommunityService communityService) {
        this.communityResourceAssembler = communityResourceAssembler;
        this.communityRepository = communityRepository;
        this.communityService = communityService;
    }

    //@GetMapping(value = "/communities/{id}/object")
    ResponseEntity<Community> get(@PathVariable Long id) {
        return this.communityRepository.findById(id)
                .map(c -> ResponseEntity.ok(c))
                .orElseThrow(() -> new CommunityNotFoundException(id));
    }


    @PostMapping(value = "/communities/{id}/add-member")
    ResponseEntity<Resource<Community>> addMemberToCommunity(@PathVariable Long id, @RequestBody Set<Long> members) {
        return communityService.addMembers(id, members);
    }
}
