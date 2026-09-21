package com.xfy.nabu.search.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.xfy.nabu.api.search.dto.TopicSearchDocDTO;
import com.xfy.nabu.common.result.PageResult;
import com.xfy.nabu.search.domain.TopicIndexDocument;
import com.xfy.nabu.search.repository.TopicIndexRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchBizServiceImpl implements SearchBizService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final TopicIndexRepository topicIndexRepository;

    public SearchBizServiceImpl(ElasticsearchOperations elasticsearchOperations,
                                 TopicIndexRepository topicIndexRepository) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.topicIndexRepository = topicIndexRepository;
    }

    @Override
    public PageResult<TopicSearchDocDTO> searchTopics(String keyword, long pageNum, long pageSize) {
        if (keyword == null || keyword.isBlank()) {
            return PageResult.empty(pageNum, pageSize);
        }
        // multi_match：title + content 两个字段联合匹配，title 权重（boost）更高
        Query multiMatchQuery = Query.of(q -> q.multiMatch(m -> m
                .query(keyword)
                .fields("title^3", "content")
        ));
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(multiMatchQuery)
                .withPageable(PageRequest.of((int) (pageNum - 1), (int) pageSize))
                .build();

        SearchHits<TopicIndexDocument> hits = elasticsearchOperations.search(nativeQuery, TopicIndexDocument.class);
        List<TopicSearchDocDTO> records = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(records, hits.getTotalHits(), pageNum, pageSize);
    }

    @Override
    public void upsert(TopicIndexDocument document) {
        topicIndexRepository.save(document);
    }

    @Override
    public void delete(String topicId) {
        if (topicId == null) {
            return;
        }
        topicIndexRepository.deleteById(topicId);
    }

    private TopicSearchDocDTO toDTO(TopicIndexDocument document) {
        if (document == null) {
            return null;
        }
        TopicSearchDocDTO dto = new TopicSearchDocDTO();
        dto.setId(document.getId() == null ? null : Long.valueOf(document.getId()));
        dto.setBoardId(document.getBoardId());
        dto.setAuthorId(document.getAuthorId());
        dto.setTitle(document.getTitle());
        dto.setContent(document.getContent());
        dto.setCreatedAtEpochMillis(document.getCreatedAtEpochMillis());
        return dto;
    }
}
