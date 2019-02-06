package io.suffragium.common.entity.community;

import io.suffragium.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Community extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long creatorAccountId;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<CommunityMember> members = new HashSet<>();
    private String title;
    private String description;

    public Community(Long creatorAccountId, String title, String description) {
        this.creatorAccountId = creatorAccountId;
        this.title = title;
        this.description = description;
    }

    public Community(Community c) {
        this.creatorAccountId = c.getCreatorAccountId();
        this.title = c.getTitle();
        this.description = c.getDescription();
    }

    public Community addMembers(Set<Long> members) {
        this.members.addAll(members.stream().map(id -> new CommunityMember(id)).collect(Collectors.toList()));
        return this;
    }
}
