package io.suffragium.CommunityService.service;

import io.suffragium.CommunityService.community.CommunityRepository;
import io.suffragium.CommunityService.controller.CommunityNotFoundException;
import io.suffragium.CommunityService.controller.CommunityResourceAssembler;
import io.suffragium.common.entity.community.Community;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;

@Service
public class CommunityService {
    @Autowired
    private CommunityRepository repository;
    private CommunityResourceAssembler resourceAssembler;

    public ResponseEntity<Resource<Community>> addMembers(Long id, Set<Long> members) {
        return repository.findById(id)
                .map(c -> {
                    repository.save(c.addMembers(members));
                    Resource<Community> communityResource = resourceAssembler.toResource(c);
                    URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
                    return ResponseEntity.created(selfLink).body(communityResource);
                })
                .orElseThrow(() -> new CommunityNotFoundException(id));
    }
}
