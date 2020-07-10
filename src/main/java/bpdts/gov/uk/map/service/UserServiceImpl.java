package bpdts.gov.uk.map.service;

import bpdts.gov.uk.map.model.User;
import bpdts.gov.uk.map.repo.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provide user service implementation
 */
@Service
public class UserServiceImpl implements UserService {

    Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public static final double LONDON_LAT = 51.509865;
    public static final double LONDON_LON = -0.118092;
    public static final int MAX_DISTANCE = 50;

    @Value("${spring.data.elasticsearch.dataSource.uri}")
    private String userDataSourceEndPoint;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RestHighLevelClient client;

    /**
     * if the user index is empty, then index all users that are pulled from a rest web api
     */
    @PostConstruct
    public void init() {
        if (userRepo.count() > 0)
            return;
        log.info("The user index is empty, so starting to pull users and index them.");
        ResponseEntity<User[]> response = restTemplate.getForEntity(userDataSourceEndPoint, User[].class);
        User[] users = response.getBody();
        if (users == null || users.length == 0)
            return;
        Stream.of(users).peek(u -> u.setLocation(new GeoPoint(u.getLatitude(), u.getLongitude()))).forEach(u -> userRepo.save(u));
        log.info("Completed the users indexing.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findUsersNearByEsIndex() {
        GeoDistanceQueryBuilder qb = QueryBuilders.geoDistanceQuery("location")
                .point(LONDON_LAT, LONDON_LON)
                .distance(MAX_DISTANCE, DistanceUnit.MILES);

        SearchSourceBuilder source = new SearchSourceBuilder().query(qb);
        SearchRequest searchRequest = new SearchRequest("user");
        searchRequest.source(source);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            return Arrays.stream(searchResponse.getHits()
                    .getHits())
                    .map(h -> toUser(h.getSourceAsMap()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Exception thrown :" + e.getMessage());
        }
        return Collections.emptyList();
    }

    private User toUser(Map<String, Object> map) {
        User user = new ObjectMapper().convertValue(map, User.class);
        user.setLocation(null);
        return user;
    }

}
