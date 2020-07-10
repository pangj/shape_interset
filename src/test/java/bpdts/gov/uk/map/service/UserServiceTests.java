package bpdts.gov.uk.map.service;

import bpdts.gov.uk.map.model.User;
import bpdts.gov.uk.map.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test the User Service implemenation
 */
@SpringBootTest
class UserServiceTests {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.data.elasticsearch.dataSource.uri}")
    private String userDataSourceEndPoint;

    @Test
    void TestCreateUserIndex() {
        userRepo.deleteAll();
        assertEquals(0, userRepo.count());
        ((UserServiceImpl) userService).init();
        assertEquals(1000, userRepo.count());
    }

    /**
     * the solution to the problem of "finding users within 50 miles distance" is
     * by indexing the GeoPoint field in elastic search server, the solution is likely
     * the one that is used in the real world for its scalability.
     * Another solution is doing an algorithm exercise, which iterator through all the users
     * and use the latitude and longitude of each user for calculating the distance, and the filter
     * those users that match the criteria.
     * Below we use both ways to prove each others. Of cause, behind the scene the calculation algorithm
     * of elastic search is actually the lucene api. So proof is the other aspects of the solution
     */
    @Test
    void testTwoWaysGetTheSameResult() {
        List<User> indexResult = userService.findUsersNearByEsIndex();
        List<User> luceneResult = findUsersWithin50MilesByLuceneAPI();
        assertEquals(indexResult.size(), luceneResult.size());
        assertTrue(indexResult.stream().allMatch(u -> luceneResult.contains(u)));
    }

    private List<User> findUsersWithin50MilesByLuceneAPI() {
        return findAllUsers().stream().filter(u -> calculateDistanceInMiles(UserServiceImpl.LONDON_LAT, UserServiceImpl.LONDON_LON, u.getLatitude(), u.getLongitude()) <= 50D)
                .collect(Collectors.toList());
    }

    private List<User> findAllUsers() {
        ResponseEntity<User[]> response = restTemplate.getForEntity(userDataSourceEndPoint, User[].class);
        User[] users = response.getBody();
        return users == null || users.length == 0 ? Collections.emptyList() : Arrays.asList(users);
    }

    double calculateDistanceInMiles(double lat1, double long1, double lat2, double long2) {
        double dist = org.apache.lucene.util.SloppyMath.haversinMeters(lat1, long1, lat2, long2);
        return dist / 1609.34;
    }
}
