package net.shyshkin.study.webfluxpatterns.sec01.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webfluxpatterns.sec01.client.ProductClient;
import net.shyshkin.study.webfluxpatterns.sec01.client.PromotionClient;
import net.shyshkin.study.webfluxpatterns.sec01.client.ReviewClient;
import net.shyshkin.study.webfluxpatterns.sec01.dto.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAggregatorService {

    private final ProductClient productClient;
    private final PromotionClient promotionClient;
    private final ReviewClient reviewClient;

    public Mono<ProductAggregate> aggregate(Integer id) {
        return Mono.zip(
                        productClient.getProduct(id),
                        promotionClient.getPromotion(id),
                        reviewClient.getReviews(id))
                .map(t -> toDto(t.getT1(), t.getT2(), t.getT3()));
    }

    private ProductAggregate toDto(ProductResponse product, PromotionResponse promotion, List<Review> reviews) {
        var price = Price.builder()
                .listPrice(product.getPrice())
                .discount(promotion.getDiscount())
                .endDate(promotion.getEndDate())
                .discountedPrice(product.getPrice() * 0.01 * (100 - promotion.getDiscount()))
                .amountSaved(product.getPrice() * promotion.getDiscount() / 100)
                .build();
        return ProductAggregate.builder()
                .id(product.getId())
                .category(product.getCategory())
                .description(product.getDescription())
                .price(price)
                .reviews(reviews)
                .build();
    }

}
