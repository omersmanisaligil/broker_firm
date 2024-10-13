package com.inghubs.broker_firm.repository;

import com.inghubs.broker_firm.entity.Asset;
import com.inghubs.broker_firm.entity.User;
import com.inghubs.broker_firm.enums.ROLE;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@TestInstance(Lifecycle.PER_CLASS)
public class AssetRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AssetRepository assetRepository;

    private static User customer1;

    private static User customer2;

    private static Asset assetTRY1;

    private static Asset assetTRY2;

    private static Asset assetEUR1;

    private static Asset assetEUR2;

    @BeforeAll
    public void setUp(){
        customer1 = new User();
        customer1.setUsername("cust1");
        customer1.setPassword("cust345");
        customer1.setRole(ROLE.CUSTOMER);
        User cust1Saved = userRepository.save(customer1);

        customer2 = new User();
        customer2.setUsername("cust2");
        customer2.setPassword("cust456");
        customer2.setRole(ROLE.CUSTOMER);
        User cust2Saved = userRepository.save(customer2);

        List<User> users = Arrays.asList(customer1,customer2);

        assetTRY1 = new Asset();
        assetTRY1.setName("TRY");
        assetTRY1.setSize(100.0);
        assetTRY1.setUsableSize(75.0);
        assetTRY1.setUser(cust2Saved);

        assetTRY2 = new Asset();
        assetTRY2.setName("TRY");
        assetTRY2.setSize(100.0);
        assetTRY2.setUsableSize(75.0);
        assetTRY2.setUser(cust1Saved);

        assetEUR1 = new Asset();
        assetEUR1.setName("EUR");
        assetEUR1.setSize(100.0);
        assetEUR1.setUsableSize(75.0);
        assetEUR1.setUser(cust2Saved);

        assetEUR2 = new Asset();
        assetEUR2.setName("EUR");
        assetEUR2.setSize(100.0);
        assetEUR2.setUsableSize(75.0);
        assetEUR2.setUser(cust1Saved);

        assetRepository.saveAll(Arrays.asList(assetTRY1,assetTRY2,assetEUR1,assetEUR2));
    }

    @Test
    public void testFindByUserId(){
        List<Asset> assets = assetRepository.findByUserId(customer1.getId());
        assets.forEach(asset -> {
            assertThat(asset.getUser().getId()).isEqualTo(customer1.getId());
        });
    }

    @Test
    public void testFindByNameAndUserId(){
        Asset asset = assetRepository.findByNameAndUserId("TRY",customer1.getId()).orElse(null);
        assertThat(asset).isNotNull();
        assertThat(asset.getUser().getId()).isEqualTo(customer1.getId());
        assertThat(asset.getName()).isEqualTo("TRY");
    }

}
