package net.shyshkin.study.webfluxpatterns.sec07.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webfluxpatterns.sec07.client.ProductClient;
import net.shyshkin.study.webfluxpatterns.sec07.client.ReviewClient;
import net.shyshkin.study.webfluxpatterns.sec07.dto.Product;
import net.shyshkin.study.webfluxpatterns.sec07.dto.ProductAggregate;
import net.shyshkin.study.webfluxpatterns.sec07.dto.Review;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAggregatorService {

    private final ProductClient productClient;
    private final ReviewClient reviewClient;

    public Mono<ProductAggregate> aggregate(Integer id) {
        return Mono.zip(
                        productClient.getProduct(id),
                        reviewClient.getReviews(id))
                .map(t -> toDto(t.getT1(), t.getT2()));
    }

    private ProductAggregate toDto(Product product, List<Review> reviews) {

        return ProductAggregate.builder()
                .id(product.getId())
                .category(product.getCategory())
                .description(product.getDescription())
                .reviews(reviews)
                .build();
    }

}
