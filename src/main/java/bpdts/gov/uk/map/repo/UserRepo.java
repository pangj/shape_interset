package bpdts.gov.uk.map.repo;

import bpdts.gov.uk.map.model.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserRepo extends ElasticsearchRepository<User, Long> {
}
