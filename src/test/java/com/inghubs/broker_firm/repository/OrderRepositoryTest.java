package com.inghubs.broker_firm.repository;

import com.inghubs.broker_firm.entity.Asset;
import com.inghubs.broker_firm.entity.Order;
import com.inghubs.broker_firm.entity.User;
import com.inghubs.broker_firm.enums.ROLE;
import com.inghubs.broker_firm.enums.SIDE;
import com.inghubs.broker_firm.enums.STATUS;
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
public class OrderRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    OrderRepository orderRepository;

    private static User customer1;

    private static User customer2;

    private static Asset assetTRY1;

    private static Asset assetTRY2;

    private static Asset assetEUR1;

    private static Asset assetEUR2;

    private static Order orderBuyEurCust1;

    private static Order orderSellEurCust2;

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

        orderBuyEurCust1 = new Order();
        orderBuyEurCust1.setAssetName("EUR");
        orderBuyEurCust1.setSize(10.0);
        orderBuyEurCust1.setOrderSide(SIDE.BUY);
        orderBuyEurCust1.setStatus(STATUS.PENDING);
        orderBuyEurCust1.setUser(cust1Saved);

        orderSellEurCust2 = new Order();
        orderSellEurCust2.setAssetName("EUR");
        orderSellEurCust2.setSize(10.0);
        orderSellEurCust2.setOrderSide(SIDE.SELL);
        orderSellEurCust2.setStatus(STATUS.PENDING);
        orderSellEurCust2.setUser(cust2Saved);

        orderRepository.saveAll(Arrays.asList(orderBuyEurCust1,orderSellEurCust2));
    }

    @Test
    public void testFindByUserId(){
        List<Order> orders = orderRepository.findByUserId(customer1.getId());
        orders.forEach(order -> {
            assertThat(order.getUser().getId()).isEqualTo(customer1.getId());
        });
    }

    @Test
    public void testNewlyCreatedOrderStatus(){
        List<Order> orders = orderRepository.findAll();
        orders.forEach(order -> {
            assertThat(order.getStatus()).isEqualTo(STATUS.PENDING);
        });
    }
}
