package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.domain.documents.PatientDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientElasticRepository extends ElasticsearchRepository<PatientDocument, Long> {

    @Query("""
            {
            "multi_match":{
                    "query": "#{#params}",
                    "fields": ["firstName", "lastName", "email"],
                    "type": "best_fields"
                }
            }
            """)
    Page<PatientDocument> findByParams(String params, Pageable pageable);
}
