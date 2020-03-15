package test.testactive.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.testactive.domain.*;
import test.testactive.food.Food;
import test.testactive.repository.FoodRepository;
import test.testactive.repository.MemberRepository;
import test.testactive.repository.OrderRepository;
import test.testactive.repository.StoreRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final FoodRepository foodRepository;
    private final StoreRepository storeRepository;


    /**
     * 주문 / 취소 / 검색 로직을 담겨 있습니다.
     */

    //주문
    @Transactional
    public Long order(Long memberId, Long foodId, int count, int qu) {


        //member food의 각각의 엔티티 조회 id로
        Member member = memberRepository.findOne(memberId);
        Food food = foodRepository.findOne(foodId);

        // delivery에 주소를 세팅
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //음식 주문로직 -> 음식 , 가격 , 개수가 담겨 있음 -> 문제는 추후 계산 문제
        Orderfood orderfood = Orderfood.createOrderfood(food, food.getPrice(), count);

        //주문에는 주문시킨사람, 배송, 음식배송 정보가 담겨 있음 -> 버전업에서는 다른 여러가지 주문에 대한 사항추가
        Order order = Order.createOrder(member, delivery, qu, orderfood); //stock, status가 저장이안됨.

        //이것을 orderRepository에서 받아와 주문을 저장한다.
        orderRepository.save(order);

        //그래서 주문의 id를 가져와 반환한다. 각각의 정보를 다 담아서
        return order.getId(); //오더정보

    }
    //취소

    @Transactional
    public void cancelOrder(Long orderId, Long foodId, Long StoreId) {

        //주문 내역 조회 -> 내정보에서 확인
        Order order = orderRepository.findOne(orderId); // STATUS
        Food food = foodRepository.findOne(foodId); //음식이름 ,가격
        Store store = storeRepository.findOne(StoreId); //가게이름, 번호

        //주문 취소 로직
        order.cancel();
    }

    @Transactional
    public void cancelbasketOrder(Long foodId, Long orderId) {

        Order order = orderRepository.findOne(orderId);
        Food food = foodRepository.findOne(foodId); //음식이름 ,가격
        //주문 취소 로직

        order.basket_cancel(order.getStockQuantity());
    }
}

    //검색
//    public List<Order> findOrders(OrderSearch orderSearch) {
//        return  orderRepository.findAll(orderSearch);
//
//    }

//    @Transactional(readOnly =true)
//    public List<OrderListResponseDto> b_findOrders(){return orderRepository.b_findAll().stream().map(OrderListResponseDto::new).collect(Collectors.toList()); }
//}

//cascade -> 정보 다날려~