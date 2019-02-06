package io.suffragium.CommunityService.controller;

import io.suffragium.common.entity.community.Community;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;

@Component
public class CommunityResourceAssembler implements ResourceAssembler<Community, Resource<Community>> {

    @Override
    public Resource<Community> toResource(Community community) {
        Resource<Community> communityResource = new Resource<>(community);
        URI selfUri = MvcUriComponentsBuilder
                .fromMethodCall(
                    MvcUriComponentsBuilder.on(CommunityRestController.class)
                .getCommunityResource(community.getId())
        ).buildAndExpand().toUri();
       /* URI fullObjectUri = MvcUriComponentsBuilder
                .fromMethodCall(
                        MvcUriComponentsBuilder.on(CommunityRestController.class)
                                .get(community.getId())
                ).buildAndExpand().toUri();*/

        communityResource.add(new Link(selfUri.toString(), "self"));
        //communityResource.add(new Link(fullObjectUri.toString(), "full-object"));
        return communityResource;
    }
}
