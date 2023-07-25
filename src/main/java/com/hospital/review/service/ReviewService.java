package com.hospital.review.service;

import com.hospital.review.domain.Hospital;
import com.hospital.review.domain.Review;
import com.hospital.review.domain.dto.ReviewCreateRequest;
import com.hospital.review.domain.dto.ReviewCreateResponse;
import com.hospital.review.domain.dto.ReviewReadResponse;
import com.hospital.review.repository.HospitalRepository;
import com.hospital.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ReviewService {

    private final HospitalRepository hospitalRepository;
    private final ReviewRepository reviewRepository;

    public ReviewService(HospitalRepository hospitalRepository, ReviewRepository reviewRepository) {
        this.hospitalRepository = hospitalRepository;
        this.reviewRepository = reviewRepository;
    }

    // 리뷰 등록
    public ReviewCreateResponse createReview(ReviewCreateRequest dto) {

        // Hospital 불러오기
        Optional<Hospital> hospitalOptional = hospitalRepository.findById(dto.getHospitalId());

        //dto -> Review using Builder (ReviewEntity 만들기)
        Review review = Review.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .patientName(dto.getUsername())
                .hospital(hospitalOptional.get())
                .build();
        //저장
        Review savedReview = reviewRepository.save(review);

        return ReviewCreateResponse.builder()
                .username(savedReview.getPatientName())
                .title(savedReview.getTitle())
                .content(savedReview.getContent())
                .message("리뷰 등록이 성공했습니다.")
                .build();
    }

    // 단건 조회
    public Review getReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 id가 없습니다"));
        return review;
    }

    // 특정 병원의 리뷰 전체 조회
    public List<ReviewReadResponse> findAllByHospitalId(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id가 없습니다."));
        List<ReviewReadResponse> reviews = reviewRepository.findByHospital(hospital)
                .stream().map(review -> ReviewReadResponse.builder()
                        .title(review.getTitle())
                        .content(review.getContent())
                        .patientName(review.getPatientName())
                        .hospitalName(review.getHospital().getHospitalName())
                        .build())
                .collect(Collectors.toList());
        return reviews;
    }

}
