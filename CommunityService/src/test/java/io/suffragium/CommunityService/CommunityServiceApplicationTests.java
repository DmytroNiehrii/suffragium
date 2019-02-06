package io.suffragium.CommunityService;

import com.mysql.cj.exceptions.AssertionFailedException;
import io.suffragium.CommunityService.community.CommunityRepository;
import io.suffragium.common.entity.community.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles(profiles = "test")
public class CommunityServiceApplicationTests {
	@Autowired
	private CommunityRepository communityRepository;
    @Autowired
	private ApplicationContext applicationContext;

    public void contextLoads() throws Throwable {
        Assert.assertNotNull("the application context should have loaded.", this.applicationContext);
    }

	@Test
	public void customerTest() {
		Community community = new Community(1L, "First community", "First community description");

		community = communityRepository.save(community);
		Community persistedResult = communityRepository.findById(community.getId()).get();

		Assert.assertNotNull(persistedResult.getId());
		Assert.assertTrue("Stored value is not matched with expected", persistedResult.getCreatorAccountId().equals(community.getCreatorAccountId()));
        Assert.assertNotNull(persistedResult.getCreatedAt());
        Assert.assertNotNull(persistedResult.getLastModified());

        communityRepository.findByTitleContaining(community.getTitle()).orElseThrow(
                () -> new AssertionFailedException(new RuntimeException("there's supposed to be a matching record!"))
        );
	}

}

