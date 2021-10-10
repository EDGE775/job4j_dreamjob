package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.util.Collection;

public interface Store {
    Collection<Post> findAllPosts();

    Collection<Candidate> findAllCandidates();

    Collection<Candidate> findCandidatesPerDay();

    void savePost(Post post);

    void saveCandidate(Candidate candidate);

    void deleteCandidateById(int id);

    Post findPostById(int id);

    Candidate findCandidateById(int id);

    Collection<User> findAllUsers();

    void saveUser(User user);

    User findUserByEmail(String eMail);

    Collection<City> findAllCities();

    City findCityByName(String name);
}
