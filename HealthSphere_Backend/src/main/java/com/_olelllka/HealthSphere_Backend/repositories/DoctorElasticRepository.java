package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.domain.documents.DoctorDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DoctorElasticRepository extends ElasticsearchRepository<DoctorDocument, Long> {

    @Query("""
            {
            "multi_match":{
                    "query": "#{#params}",
                    "fields": ["firstName", "lastName"],
                    "type": "best_fields"
                }
            }
            """)
    Page<DoctorDocument> findByFirstAndLastnames(String params, Pageable pageable);

}
