package ru.job4j.dream.store;

import ru.job4j.dream.model.Post;

public class PsqlMain {
    public static void main(String[] args) {
        Store store = PsqlStore.instOf();
        store.savePost(new Post(0, "Java Job 1"));
        store.savePost(new Post(0, "Java Job 2"));
        store.savePost(new Post(0, "Java Job 3"));
        for (Post post : store.findAllPosts()) {
            System.out.println(post.getId() + " " + post.getName());
        }
        System.out.println();
        store.savePost(new Post(1, "Java Middle Job 1"));
        store.savePost(new Post(2, "Java Middle Job 2"));
        for (Post post : store.findAllPosts()) {
            System.out.println(post.getId() + " " + post.getName());
        }
        System.out.println();
        System.out.println(store.findPostById(1));
    }
}