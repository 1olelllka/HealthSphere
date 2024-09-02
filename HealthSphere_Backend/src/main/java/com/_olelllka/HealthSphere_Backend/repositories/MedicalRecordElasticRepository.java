package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.domain.documents.MedicalRecordDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordElasticRepository extends ElasticsearchRepository<MedicalRecordDocument, Long> {

    @Query("""
    {
        "bool": {
            "must": [{
                "match": {
                    "user_id": "#{#id}"
                }
            }],
            "filter": [
            {
                "range": {
                    "recordDate": {
                        "gte": "#{#from}",
                        "lte": "#{#to}",
                        "format": "yyyy-MM-dd"
                    }
                }
            }],
            "should": [{
                "match": {
                    "diagnosis": "#{#diagnosis}"
                }
            }]
        }
    }
    """)
    Page<MedicalRecordDocument> findByParams(Long id, String diagnosis, String from, String to, Pageable pageable);
}