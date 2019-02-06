package io.suffragium.CommunityService.controller;

import lombok.Getter;

public class CommunityNotFoundException extends RuntimeException {

    @Getter
    private final Long id;

    public CommunityNotFoundException(Long id) {
        super("community-not-found-" + id);
        this.id = id;
    }

}
