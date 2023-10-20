package com.test.hibernate.model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Artist {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany
    private List<Song> songs;

    @ManyToMany
    private Set<Movie> movies;


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public void setMovies(Set<Movie> movies) {
        this.movies = movies;
    }

    public Set<Movie> getMovies() {
        return movies;
    }
}
