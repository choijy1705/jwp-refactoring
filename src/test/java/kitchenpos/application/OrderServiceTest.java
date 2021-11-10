package kitchenpos.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import kitchenpos.Fixtures;
import kitchenpos.dao.MenuRepository;
import kitchenpos.dao.OrderLineItemRepository;
import kitchenpos.dao.OrderRepository;
import kitchenpos.dao.OrderTableRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import kitchenpos.dto.OrderLineItemRequest;
import kitchenpos.dto.OrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;
    private OrderLineItem orderLineItem;
    private List<OrderLineItem> orderLineItems;
    private Order order;
    private OrderTable orderTable;
    private Menu menu;

    @Mock
    private MenuService menuService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderLineItemService orderLineItemService;

    @Mock
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        TableGroup tableGroup = new TableGroup(1L);
        orderTable = new OrderTable(1L, tableGroup, 1, false);
        order = new Order(orderTable, OrderStatus.COOKING);
        menu = Fixtures.makeMenu();

        orderLineItem = new OrderLineItem(1L, order, menu, 1L);

        orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        order.addOrderLineItems(orderLineItems);

    }

    @DisplayName("order 생성")
    @Test
    void create() {
        given(orderTableService.findById(anyLong()))
            .willReturn(orderTable);
        given(orderRepository.save(any(Order.class)))
            .willReturn(order);
        given(menuService.findById(anyLong()))
            .willReturn(menu);

        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(1L, 1);

        OrderRequest orderRequest =
            new OrderRequest(
                1L,
                OrderStatus.COOKING,
                Collections.singletonList(orderLineItemRequest));

        orderService.create(orderRequest);

        verify(orderRepository).save(any(Order.class));
        verify(orderLineItemService).saveAll(anyList());
    }

    @DisplayName("order 불러오기")
    @Test
    void list() {
        List<Order> orders = new ArrayList<>();
        orders.add(this.order);

        given(orderRepository.findAll())
            .willReturn(orders);

        orderService.list();

        verify(orderRepository).findAll();
    }

    @DisplayName("주문상태 바꾸기")
    @Test
    void changeOrder() {
        given(orderRepository.findById(anyLong()))
            .willReturn(Optional.of(order));

        OrderRequest orderRequest = new OrderRequest(1L, OrderStatus.COOKING, null);

        orderService.changeOrderStatus(1L, orderRequest);
    }

}
