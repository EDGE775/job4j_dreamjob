package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());

    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            cfg.load(in);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM post")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(new Post(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка при получении списка вакансий из БД", e);
        }
        return posts;
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "select "
                             + "c.id as id, "
                             + "c.name as name, "
                             + "c.created as created, "
                             + "c.city_id as city_id, "
                             + "city.name as city_name "
                             + "from candidate as c "
                             + "left join city "
                             + "on c.city_id = city.id;")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(
                            new Candidate(
                                    it.getInt("id"),
                                    it.getString("name"),
                                    new City(
                                            it.getInt("city_id"),
                                            it.getString("city_name")),
                                    it.getTimestamp("created").toLocalDateTime()));
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка при получении списка кандидатов из БД", e);
        }
        return candidates;
    }

    @Override
    public Collection<Candidate> findCandidatesPerDay() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "select "
                             + "c.id as id, "
                             + "c.name as name, "
                             + "c.created as created, "
                             + "c.city_id as city_id, "
                             + "city.name as city_name "
                             + "from candidate as c "
                             + "left join city "
                             + "on c.city_id = city.id "
                             + "where c.created > (now() - interval '1 DAY');")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(
                            new Candidate(
                                    it.getInt("id"),
                                    it.getString("name"),
                                    new City(
                                            it.getInt("city_id"),
                                            it.getString("city_name")),
                                    it.getTimestamp("created").toLocalDateTime()));
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка при получении списка кандидатов, добавленных за последние сутки из БД", e);
        }
        return candidates;
    }


    @Override
    public void savePost(Post post) {
        if (post.getId() == 0) {
            createPost(post);
        } else {
            updatePost(post);
        }
    }

    private Post createPost(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =
                     cn.prepareStatement("INSERT INTO post(name) VALUES (?)",
                             PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка при создании вакансии", e);
        }
        return post;
    }

    private void updatePost(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =
                     cn.prepareStatement("update post set name = ? where id = ?")) {
            ps.setString(1, post.getName());
            ps.setInt(2, post.getId());
            ps.execute();
        } catch (Exception e) {
            LOG.error("Ошибка при обновлении вакансии", e);
        }
    }

    @Override
    public Post findPostById(int id) {
        Post post = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from post where id = ?")) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    post = new Post(
                            resultSet.getInt("id"),
                            resultSet.getString("name")
                    );
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка при получении вакансии по ID", e);
        }
        return post;
    }

    @Override
    public Candidate findCandidateById(int id) {
        Candidate candidate = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "select "
                             + "c.id as id, "
                             + "c.name as name, "
                             + "c.created as created, "
                             + "c.city_id as city_id, "
                             + "city.name as city_name "
                             + "from candidate as c "
                             + "left join city "
                             + "on c.city_id = city.id "
                             + "where c.id = ?;")) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    candidate = new Candidate(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            new City(
                                    resultSet.getInt("city_id"),
                                    resultSet.getString("city_name")),
                            resultSet.getTimestamp("created").toLocalDateTime());
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка при получении кандидата по ID", e);
        }
        return candidate;
    }

    @Override
    public void saveCandidate(Candidate candidate) {
        if (candidate.getId() == 0) {
            createCandidate(candidate);
        } else {
            updateCandidate(candidate);
        }
    }

    private Candidate createCandidate(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =
                     cn.prepareStatement(
                             "INSERT INTO candidate(name, created, city_id) VALUES (?, ?, ?)",
                             PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());
            ps.setTimestamp(2, Timestamp.valueOf(candidate.getCreated()));
            ps.setInt(3, candidate.getCity().getId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка при создании кандидата", e);
        }
        return candidate;
    }

    private void updateCandidate(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =
                     cn.prepareStatement(
                             "update candidate set name = ?, city_id = ? where id = ?")) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getCity().getId());
            ps.setInt(3, candidate.getId());
            ps.execute();
        } catch (Exception e) {
            LOG.error("Ошибка при обновлении кандидата", e);
        }
    }

    @Override
    public void deleteCandidateById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =
                     cn.prepareStatement("delete from candidate where id = ?")) {
            ps.setInt(1, id);
            ps.execute();
        } catch (Exception e) {
            LOG.error("Ошибка при удалении кандидата по ID", e);
        }
    }

    @Override
    public Collection<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM users")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    users.add(new User(
                            it.getInt("id"),
                            it.getString("name"),
                            it.getString("email"),
                            it.getString("password")));
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка при получении списка юзеров из БД", e);
        }
        return users;
    }

    @Override
    public void saveUser(User user) {
        if (user.getId() == 0) {
            createUser(user);
        } else {
            updateUser(user);
        }

    }

    private void updateUser(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =
                     cn.prepareStatement("update users set name = ?, email = ?, password = ? where id = ?")) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getId());
            ps.execute();
        } catch (Exception e) {
            LOG.error("Ошибка при обновлении юзера", e);
        }
    }

    private User createUser(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =
                     cn.prepareStatement("INSERT INTO users(name, email, password) VALUES (?, ?, ?)",
                             PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка при создании юзера", e);
        }
        return user;
    }

    @Override
    public User findUserByEmail(String eMail) {
        User user = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from users where email = ?")) {
            ps.setString(1, eMail);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getString("password"));
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка при получении юзера по email", e);
        }
        return user;
    }

    @Override
    public Collection<City> findAllCities() {
        List<City> cities = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM city")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    cities.add(new City(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка при получении списка городов из БД", e);
        }
        return cities;
    }

    @Override
    public City findCityByName(String name) {
        City city = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from city where name = ?")) {
            ps.setString(1, name);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    city = new City(
                            resultSet.getInt("id"),
                            resultSet.getString("name"));
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка при получении юзера по email", e);
        }
        return city;
    }
}